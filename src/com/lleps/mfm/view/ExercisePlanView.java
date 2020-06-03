package com.lleps.mfm.view;

import com.alee.laf.button.WebButton;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lleps.mfm.Resources;
import com.lleps.mfm.Storage;
import com.lleps.mfm.Utils;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.ExercisePlan;
import com.sun.istack.internal.Nullable;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.html.Option;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private JPanel contentPane;
    private JButton sendEmailButton;
    private JButton deletePlanButton;
    private JTable exercises;
    private JButton savePDFButton;
    private JScrollPane baseScroll;
    private ExercisePlan plan;

    private Category category;
    private Client client;

    public ExercisePlanView(Category category, Client client, ExercisePlan plan) {
        super();

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

        exercises.setModel(new DefaultTableModel(plan.getExercises().clone(), ExercisePlan.defaultColumns));
        exercises.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        if (client != null) {
            savePDFButton.addActionListener(e -> {
                Utils.doUsingNativeLAF(() -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File("."));
                    chooser.setSelectedFile(new File(plan.getName() + "-" + client.getFirstName() + "-" + client.getLastName() + ".pdf"));
                    int option = chooser.showSaveDialog(this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        FloatingMessageView.hide();
                        Path path = chooser.getSelectedFile().toPath();

                        try {
                            if (!Files.exists(path) || (Files.exists(path) && path.toString().endsWith(".pdf"))) {
                                Files.write(path, createExercisesPlanPDF());
                                FloatingMessageView.show("Guardando...");
                            } else {
                                JOptionPane.showMessageDialog(chooser, "No se puede sobreescribir. Elija otro nombre.");
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
        SwingUtilities.invokeLater(() -> {
            if (baseScroll != null && baseScroll.getColumnHeader() != null) {
                baseScroll.getColumnHeader().setVisible(false);
                baseScroll.revalidate();
            }
        });
    }

    private class CustomCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component rendererComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);

            if (row == 0) {
                rendererComp.setFont(new java.awt.Font("Verdana", java.awt.Font.BOLD, 12));
            }
            return rendererComp;
        }
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
        final CustomCellRenderer renderer = new CustomCellRenderer();
        exercises = new JTable() {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                // TODO Auto-generated method stub
                return renderer;
            }
        };

    }

    private byte[] createExercisesPlanPDF() throws MessagingException, DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, stream);
        writer.setInitialLeading(20f);
        document.open();

        // bg
        Image bgImg = Image.getInstance(ClassLoader.getSystemResource("resources/pdf-background.jpg"));
        bgImg.setAbsolutePosition(0, 0);
        bgImg.scaleAbsolute(document.getPageSize());
        document.add(bgImg);

        // Head
        Font headerFontBold = FontFactory.getFont("/resources/Heading-Compressed-Pro-Heavy.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 20, Font.NORMAL, new BaseColor(0xFF363636));
        Font headerFont = FontFactory.getFont("Arial",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 19, Font.NORMAL, new BaseColor(0xFF363636));

        // cell fonts
        Font cellFontBold = FontFactory.getFont("/resources/Montserrat-Regular.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13, Font.BOLD, new BaseColor(0xFF111111));

        // aux spacing font
        Font spacingFont = FontFactory.getFont("/resources/Montserrat-Regular.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8, Font.NORMAL, new BaseColor(0xFF111111));

        // Nombre
        Phrase phrase = new Phrase();
        phrase.add(new Chunk("Nombre: ", headerFontBold));
        phrase.add(new Chunk(client.getFirstName() + " " + client.getLastName(), headerFont));
        document.add(new Paragraph(" ", spacingFont)); // space
        document.add(phrase);

        // Observaciones
        phrase = new Phrase();
        phrase.add(new Chunk("Observaciones: ", headerFontBold));
        phrase.add(new Chunk(client.getObservations(), headerFont));
        document.add(new Paragraph(" ", spacingFont)); // space
        document.add(phrase);

        // Ingreso
        phrase = new Phrase();
        phrase.add(new Chunk("Ingreso: ", headerFontBold));
        phrase.add(new Chunk(client.getInscriptionDate().format(Utils.DATE_FORMATTER), headerFont));
        document.add(new Paragraph(" ", spacingFont)); // space
        document.add(phrase);

        document.add(new Paragraph(" ")); // space
        document.add(new Paragraph(" ")); // space
        document.add(new Paragraph(" ")); // space
        document.add(new Paragraph(" ")); // space

        PdfPTable table = new PdfPTable(plan.getExercises()[0].length);
        for (int row = 0; row < exercises.getRowCount(); row++) {
            for (int column = 0; column < exercises.getColumnCount(); column++) {
                if (row == 0) {
                    Paragraph p = new Paragraph(exercises.getValueAt(row, column).toString(), cellFontBold);
                    PdfPCell cell = new PdfPCell(p);
                    cell.setBackgroundColor(WebColors.getRGBColor("#fce404"));
                    table.addCell(cell);
                } else {
                    table.addCell(new Paragraph(exercises.getValueAt(row, column).toString() + " ",
                            FontFactory.getFont("/resources/Montserrat-Regular.ttf",
                            BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13, Font.NORMAL, new BaseColor(0xFF111111))));
                }
            }
        }
        document.add(table);
        document.close();

        FloatingMessageView.show("Enviando...");
        return stream.toByteArray();
    }

    public static void main(String[] args) {
        String[][] exc = ExercisePlan.getEmptyExercises();
        exc[0] = new String[] { "Ejercicio", "Tiempo", "Serie", "Descanso" };
        for (int i = 1; i < 30; i++) {
            exc[i][0] = "Spinning";
            exc[i][1] = "Bici";
            exc[i][2] = "Biceps";
            exc[i][3] = "Mancuernas";
        }

        ExercisePlanView view = new ExercisePlanView(
                new Category(
                        "",
                        0,
                        new ArrayList<Client>(),
                        new ArrayList<>(),
                        new ArrayList<>()),
                new Client(0, false, 0, "Leandro", "Herrera",
                        "ASD", "", "", LocalDate.now(), "-",
                        new ArrayList<>()), new ExercisePlan("asd", LocalDate.MAX, exc));

        try {
            Files.write(Paths.get("C:\\Users\\leand\\Desktop\\Facultad\\plan.pdf"), view.createExercisesPlanPDF());
            FloatingMessageView.show("Guardando...");
        } catch (Exception ex) {
            Utils.reportException(ex, "error exporting file");
        }
        FloatingMessageView.hide();
    }
}
