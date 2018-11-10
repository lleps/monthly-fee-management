package com.lleps.mfm.view;

import com.alee.extended.date.WebDateField;
import com.alee.laf.text.WebTextField;
import com.lleps.mfm.model.Payment;
import com.lleps.mfm.Resources;
import com.lleps.mfm.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ClientPaymentsView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox monthsComboBox;
    private JTextField amountField;
    private JLabel previousPaymentsLabel;
    private JTextArea observationsField;
    private JLabel lastPaymentsLabel;
    private JPanel datePanel;
    private ActionListener acceptButtonListener;
    private ActionListener cancelButtonListener;
    private WebDateField dateField;

    public ClientPaymentsView() {
        setContentPane(contentPane);
        setModal(true);
        setIconImage(Resources.getInstance().APP_IMAGE);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> {
            if (acceptButtonListener != null) {
                acceptButtonListener.actionPerformed(e);
            }
        });
        buttonCancel.addActionListener(e -> {
            if (cancelButtonListener != null) {
                cancelButtonListener.actionPerformed(e);
            }
        });
        setSize(new Dimension(450, 450));
    }

    public void setAcceptButtonListener(ActionListener acceptButtonListener) {
        this.acceptButtonListener = acceptButtonListener;
    }

    public void setCancelButtonListener(ActionListener cancelButtonListener) {
        this.cancelButtonListener = cancelButtonListener;
    }

    public void setAmountField(int value) {
        amountField.setText(Integer.toString(value));
    }

    public void setObservations(String observations) {
        observationsField.setText(observations);
    }

    public String getObservations() {
        return observationsField.getText();
    }

    public Optional<Integer> getAmountField() {
        try {
            return Optional.of(Integer.parseInt(amountField.getText()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public void setPreviousPayments(List<Payment> payments) {
        String string = "<html>";
        for (Payment payment : payments) {
            string += String.format("El %s pago <b><font color=green>%s</font></b> por $%d<br>",
                    payment.getEmitDate().format(Utils.DATE_FORMATTER),
                    Utils.getMonthWithYear(payment.getMonthDate()),
                    payment.getMoney());
        }
        if (payments.isEmpty()) {
            string += "Ninguno";
        }
        string += "</html>";
        previousPaymentsLabel.setText(string);
    }

    public void setSelectableMonths(List<LocalDate> months) {
        monthsComboBox.removeAllItems();
        for (LocalDate date : months) {
            monthsComboBox.addItem(new DateItem(Utils.firstUpperCase(Utils.getMonthWithYear(date)), date));
        }
    }

    public void setSelectedMonth(LocalDate month) {
        for (int i = 0; i < monthsComboBox.getItemCount(); i++) {
            if (((DateItem)monthsComboBox.getItemAt(i)).date.equals(month)) {
                monthsComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    public LocalDate getSelectedMonth() {
        return ((DateItem)monthsComboBox.getSelectedItem()).date;
    }

    public LocalDate getSelectedDate() {
        Date input = dateField.getDate();
        return input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void createUIComponents() {
        amountField = new WebTextField();
        ((WebTextField)amountField).setLeadingComponent(new JLabel(Resources.getInstance().DOLLAR_ICON));
        datePanel = new JPanel();
        dateField = new WebDateField();
        dateField.setDate(new Date());
        datePanel.add(dateField);
    }

    private class DateItem {
        String label;
        LocalDate date;

        public DateItem(String label, LocalDate date) {
            this.label = label;
            this.date = date;
        }

        public LocalDate getDate() {
            return date;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}