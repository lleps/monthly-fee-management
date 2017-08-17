package com.lleps.mfm.view;

import com.alee.laf.button.WebButton;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lleps.mfm.Resources;
import com.lleps.mfm.Storage;
import com.lleps.mfm.Utils;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.ExercisePlan;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ExercisePlanView extends JDialog {
    private JPanel contentPane;
    private JButton sendEmailButton;
    private JButton deletePlanButton;
    private JTable exercises;
    private JButton savePDFButton;
    private Category category;
    private ExercisePlan plan;
    private String[] columns = { "Ejercicio", "Series", "Repeticiones", "Pausa" };
    private Client client;

    // TODO dejar 4 columnas nada mas
    public ExercisePlanView(Category category, Client client, ExercisePlan plan) {
        this.category = category;
        this.client = client;
        this.plan = plan;

        setIconImage(Resources.getInstance().APP_IMAGE);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(sendEmailButton);
        setTitle("Plan " + plan.getName() + " (" + plan.getDate().format(Utils.DATE_FORMATTER) + ")");

        deletePlanButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "¿Seguro que quiere borrar el plan?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                List<ExercisePlan> plans = new ArrayList<>(client.getExercisePlans());
                plans.remove(plan);
                client.setExercisePlans(plans);
                dispose();
            }
        });

        exercises.setModel(new DefaultTableModel(plan.getExercises().clone(), columns));
        exercises.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        savePDFButton.addActionListener(e -> {
            Utils.doUsingNativeLAF(() -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setSelectedFile(new File(plan.getName() + "-" + client.getFirstName() + "-" + client.getLastName() + ".pdf"));
                int option = chooser.showSaveDialog(this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    try {
                        Files.write(chooser.getSelectedFile().toPath(), createExercisesPlanPDF());
                        FloatingMessageView.show("Guardando...");
                    } catch (Exception ex) {
                        Utils.reportException(ex, "error exporting file");
                    }
                    FloatingMessageView.hide();
                }
            });
        });
        sendEmailButton.addActionListener(e -> {
            String emailReceiver = (String) JOptionPane.showInputDialog(null,
                    "Enviar plan a la dirección de correo:",
                    "Enviar plan por email",
                    JOptionPane.PLAIN_MESSAGE,
                    Resources.getInstance().MAIL_ICON, null,
                    client.getMail());

            if (emailReceiver == null) {
                return;
            }

            try {
                FloatingMessageView.show("Enviando...");
                final String username = "gimnasio653vcp@gmail.com";
                final String password = "gymgymgym";
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });
                Message message = new MimeMessage(session);
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText("Plan " + plan.getName());

                byte[] bytes = createExercisesPlanPDF();
                DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
                MimeBodyPart pdfBodyPart = new MimeBodyPart();
                pdfBodyPart.setDataHandler(new DataHandler(dataSource));
                pdfBodyPart.setFileName("plan.pdf");

                MimeMultipart mimeMultipart = new MimeMultipart();
                mimeMultipart.addBodyPart(textBodyPart);
                mimeMultipart.addBodyPart(pdfBodyPart);

                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailReceiver));
                message.setSubject("Plan de ejercicios gimnasio 653");
                message.setContent(mimeMultipart);

                Transport.send(message);
                FloatingMessageView.hide();
            } catch (MessagingException | DocumentException | IOException e1) {
                Utils.reportException(e1, "Error enviando el email.");
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
        exercises.transferFocusBackward();

        // Save player exercises based on table data
        String[][] planExercises = plan.getExercises().clone();
        for (int i = 0; i < planExercises.length; i++) {
            for (int j = 0; j < planExercises[i].length; j++) {
                planExercises[i][j] = this.exercises.getValueAt(i, j).toString();
            }
        }
        plan.setExercises(planExercises);
        try {
            Storage.getInstance().saveCategoryClients(category);
        } catch (IOException e) {
            Utils.reportException(e, "error saving category");
        }
        dispose();
    }

    private void createUIComponents() {
        deletePlanButton = new WebButton(Resources.getInstance().TRASH_ICON);
        sendEmailButton = new WebButton(Resources.getInstance().MAIL_ICON);
        savePDFButton = new WebButton(Resources.getInstance().PENCIL_ICON);
        exercises = new JTable();
    }

    private byte[] createExercisesPlanPDF() throws MessagingException, DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, stream);
        writer.setInitialLeading(20f);
        document.open();
        Image img = Image.getInstance(ClassLoader.getSystemResource("resources/pdfimage.jpg"));
        document.add(img);
        Font defaultFont = FontFactory.getFont("arial", 12, Font.BOLD, BaseColor.BLACK);

        document.add(new Paragraph("Nombre: " + client.getFirstName(), defaultFont));
        document.add(new Paragraph("Apellido: " + client.getLastName(), defaultFont));
        document.add(new Paragraph("Observaciones: " + client.getObservations(), defaultFont));
        document.add(new Paragraph("Ingreso: " + client.getInscriptionDate().format(Utils.DATE_FORMATTER), defaultFont));
        document.add(new Paragraph(" ")); // space
        PdfPTable table = new PdfPTable(plan.getExercises()[0].length);
        for (String s : columns) {
            table.addCell(new Paragraph(s.toUpperCase(),
                    FontFactory.getFont("arial",
                            11,
                            Font.BOLD,
                            BaseColor.BLACK)));
        }
        for (int row = 0; row < exercises.getRowCount(); row++) {
            for (int column = 0; column < exercises.getColumnCount(); column++) {
                table.addCell(exercises.getValueAt(row, column).toString() + " ");
            }
        }
        document.add(table);
        document.close();

        // Send email
        FloatingMessageView.show("Enviando...");
        final String username = "gimnasio653vcp@gmail.com";
        final String password = "gymgymgym";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        Message message = new MimeMessage(session);
        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText("Plan " + plan.getName());

        return stream.toByteArray();
    }
}
