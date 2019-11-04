package com.lleps.mfm.view;

import com.alee.laf.button.WebButton;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This view is used to edit an ExercisePlan.
 *
 * If a client is given, an option to send as email and
 * save to disk as PDF will be available, and the plan will be saved using the default Storage instance.
 *
 * Otherwise, will be interpreted as "category plans" and saved there.
 */
public class ExercisePlanView extends JDialog {
    private static final String[] columns = { "Ejercicio", "Series", "Repeticiones" };
    private static final int rows = 40;

    private JPanel contentPane;
    private JButton sendEmailButton;
    private JButton deletePlanButton;
    private JTable exercises;
    private JButton savePDFButton;
    private ExercisePlan plan;
    private Category category;
    private Client client;

    public ExercisePlanView(Category category, Client client, ExercisePlan plan) {
        this.category = category;
        this.client = client;
        this.plan = plan;

        reshapeExercisePlan(plan, rows, columns.length);
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
                if (client != null) { // Client plan
                    List<ExercisePlan> plans = new ArrayList<>(client.getExercisePlans());
                    plans.remove(plan);
                    client.setExercisePlans(plans);
                    onCancel();
                } else { // Category plan
                    category.removePlan(plan);
                    onCancel();
                }
            }
        });

        exercises.setModel(new DefaultTableModel(plan.getExercises().clone(), columns));

        // make col 0 always 50% of width
        SwingUtilities.invokeLater(() -> {
            if (columns.length > 1) {
                int halfWidth = exercises.getWidth() / 2;
                exercises.getColumnModel().getColumn(0).setPreferredWidth(halfWidth);
                for (int i = 1; i < columns.length; i++) {
                    exercises.getColumnModel().getColumn(i).setPreferredWidth(halfWidth / (columns.length - 1));
                }
            }
        });

        exercises.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        if (client != null) {
            savePDFButton.addActionListener(e -> {
                Utils.doUsingNativeLAF(() -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File("."));
                    chooser.setFileFilter(new FileFilter() {
                        public String getDescription() {
                            return "Archivos PDF (*.pdf)";
                        }

                        public boolean accept(File f) {
                            if (f.isDirectory()) {
                                return true;
                            } else {
                                String filename = f.getName().toLowerCase();
                                return filename.endsWith(".pdf");
                            }
                        }
                    });
                    chooser.setSelectedFile(new File(plan.getName() + "-" + client.getFirstName() + "-" + client.getLastName() + ".pdf"));
                    int option = chooser.showSaveDialog(this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        try {
                            String professor = JOptionPane.showInputDialog(
                                    this,
                                    "Profesor que imprime este plan:",
                                    "Guardar plan",
                                    JOptionPane.QUESTION_MESSAGE);
                            if (professor != null) {
                                Files.write(chooser.getSelectedFile().toPath(), createExercisesPlanPDF(professor));
                                FloatingMessageView.show("Guardando...");
                            }
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

                    byte[] bytes = createExercisesPlanPDF("-");
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
        } else { // client == null
            savePDFButton.setVisible(false);
            sendEmailButton.setVisible(false);
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private static void reshapeExercisePlan(ExercisePlan plan, int rows, int cols) {
        String[][] result = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                try {
                    result[i][j] = plan.getExercises()[i][j];
                } catch (ArrayIndexOutOfBoundsException e) {
                    result[i][j] = "";
                }
            }
        }
        plan.setExercises(result);
    }

    private void onCancel() {
        exercises.transferFocusBackward();

        // Save client exercises based on table data
        String[][] planExercises = plan.getExercises().clone();
        for (int i = 0; i < planExercises.length; i++) {
            for (int j = 0; j < planExercises[i].length; j++) {
                planExercises[i][j] = this.exercises.getValueAt(i, j).toString();
            }
        }
        plan.setExercises(planExercises);
        try {
            if (client != null) {
                Storage.getInstance().saveCategoryClients(category);
            } else {
                Storage.getInstance().saveCategoryPlans(category);
            }
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

    // TODO: when init. adapt the given to the dimensions. So in all the remaining code
    //  you can ensure the array is the size you expect and makes everything easier.

    private byte[] createExercisesPlanPDF(String professor) throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, stream);
        writer.setInitialLeading(1f);

        // header pic
        document.open();
        document.bottom(10);
        Image img = Image.getInstance(ClassLoader.getSystemResource("resources/pdfimage.jpg"));
        img.setAbsolutePosition(document.right() - img.getPlainWidth(), document.top()- img.getPlainHeight()*0.7f);
        document.add(img);

        Font defaultFont = FontFactory.getFont("arial", 12, Font.BOLD, BaseColor.BLACK);

        // name + professor
        document.add(new Paragraph("Nombre: " + client.getFirstName() + " " + client.getLastName(), defaultFont));
        document.add(new Paragraph("Profesor: " + professor, defaultFont));
        document.add(new Paragraph("Plan: " + plan.getName(), defaultFont));
        document.add(new Paragraph(" "));

        // table
        PdfPTable table = new PdfPTable(columns.length);
        table.setWidthPercentage(90);
        table.setWidths(new float[] { 5, 2, 2});
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
        return stream.toByteArray();
    }
}
