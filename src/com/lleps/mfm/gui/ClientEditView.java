package com.lleps.mfm.gui;

import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import com.lleps.mfm.Resources;

import javax.swing.*;
import java.awt.event.*;

public class ClientEditView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameField;
    private JTextField lastNameField;
    private JTextField phoneNumberField;
    private JTextField homeAddressField;
    private JTextArea observationsTextArea;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JTextField mailField;
    private ActionListener acceptButtonListener;
    private ActionListener cancelButtonListener;

    public ClientEditView() {
        setIconImage(Resources.getInstance().APP_IMAGE);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> {
            if (acceptButtonListener != null && updateOKButton()) acceptButtonListener.actionPerformed(e);
        });
        buttonCancel.addActionListener(e -> {
            if (cancelButtonListener != null) cancelButtonListener.actionPerformed(e);
        });

        setSize(345, 330);
        buttonOK.setEnabled(false);
        maleRadioButton.setSelected(true);
    }

    public void setAcceptButtonListener(ActionListener acceptButtonListener) {
        this.acceptButtonListener = acceptButtonListener;
    }

    public void setCancelButtonListener(ActionListener cancelButtonListener) {
        this.cancelButtonListener = cancelButtonListener;
    }

    public void setNameField(String content) {
        nameField.setText(content);
        updateOKButton();
    }

    public String getNameField() {
        return nameField.getText();
    }

    public void setLastNameField(String content) {
        lastNameField.setText(content);
        updateOKButton();
    }

    public String getLastNameField() {
        return lastNameField.getText();
    }

    public void setPhoneNumberField(String content) {
        phoneNumberField.setText(content);
        updateOKButton();
    }

    public String getPhoneNumberField() {
        return phoneNumberField.getText();
    }

    public void setHomeAddressField(String content) {
        homeAddressField.setText(content);
        updateOKButton();
    }

    public String getHomeAddressField() {
        return homeAddressField.getText();
    }

    public void setMailField(String content) {
        mailField.setText(content);
        updateOKButton();
    }

    public String getMailField() {
        return mailField.getText();
    }

    public void setObservationsField(String content) {
        observationsTextArea.setText(content);
        updateOKButton();
    }

    public String getObservationsField() {
        return observationsTextArea.getText();
    }

    public boolean isMaleSelected() {
        return maleRadioButton.isSelected();
    }

    public void setMaleSelected(boolean male) {
        maleRadioButton.setSelected(male);
        femaleRadioButton.setSelected(!male);
    }

    private void createUIComponents() {
        nameField = new WebTextField();
        ((WebTextField)nameField).setInputPrompt("Nombre");
        ((WebTextField)nameField).setHideInputPromptOnFocus(false);
        ((WebTextField)nameField).setLeadingComponent(new JLabel(Resources.getInstance().USER_ICON));

        lastNameField = new WebTextField();
        ((WebTextField)lastNameField).setInputPrompt("Apellido");
        ((WebTextField)lastNameField).setHideInputPromptOnFocus(false);
        ((WebTextField)lastNameField).setLeadingComponent(new JLabel(Resources.getInstance().BLANK_ICON));

        phoneNumberField = new WebTextField();
        ((WebTextField)phoneNumberField).setInputPrompt("Número de celular");
        ((WebTextField)phoneNumberField).setHideInputPromptOnFocus(false);
        ((WebTextField)phoneNumberField).setLeadingComponent(new JLabel(Resources.getInstance().PHONE_ICON));

        homeAddressField = new WebTextField();
        ((WebTextField)homeAddressField).setInputPrompt("Domicilio");
        ((WebTextField)homeAddressField).setHideInputPromptOnFocus(false);
        ((WebTextField)homeAddressField).setLeadingComponent(new JLabel(Resources.getInstance().HOME_ICON));

        mailField = new WebTextField();
        ((WebTextField)mailField).setInputPrompt("Correo electrónico");
        ((WebTextField)mailField).setHideInputPromptOnFocus(false);
        ((WebTextField)mailField).setLeadingComponent(new JLabel(Resources.getInstance().MAIL_ICON));

        observationsTextArea = new WebTextArea();
        ((WebTextArea)observationsTextArea).setInputPrompt("Observaciones (opcional)");
        ((WebTextArea)observationsTextArea).setHideInputPromptOnFocus(false);

        nameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateOKButton();
            }
        });

        lastNameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateOKButton();
            }
        });

        phoneNumberField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateOKButton();
            }
        });

        homeAddressField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateOKButton();
            }
        });

        mailField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateOKButton();
            }
        });
    }

    private boolean shouldBlockOK() {
        return nameField.getText().isEmpty()
                || lastNameField.getText().isEmpty()
                || phoneNumberField.getText().isEmpty()
                || homeAddressField.getText().isEmpty()
                || mailField.getText().isEmpty();
    }

    private boolean updateOKButton() {
        boolean enabled = !shouldBlockOK();
        buttonOK.setEnabled(enabled);
        return enabled;
    }
}