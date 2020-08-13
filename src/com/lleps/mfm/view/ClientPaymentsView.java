package com.lleps.mfm.view;

import com.alee.extended.date.WebDateField;
import com.alee.laf.text.WebTextField;
import com.lleps.mfm.model.Payment;
import com.lleps.mfm.Resources;
import com.lleps.mfm.Utils;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ClientPaymentsView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox monthsComboBox;
    private JTextField amountField;
    private JTextArea observationsField;
    private JLabel lastPaymentsLabel;
    private JPanel datePanel;
    private JList paymentList;
    private ActionListener acceptButtonListener;
    private ActionListener cancelButtonListener;
    private WebDateField dateField;

    private List<Payment> previousPayments;
    private Consumer<Payment> onDeletePaymentListener;

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
        paymentList.addListSelectionListener(e -> onClickPayment(e.getFirstIndex()));
        setSize(new Dimension(600, 550));
    }

    public void setAcceptButtonListener(ActionListener acceptButtonListener) {
        this.acceptButtonListener = acceptButtonListener;
    }

    public void setCancelButtonListener(ActionListener cancelButtonListener) {
        this.cancelButtonListener = cancelButtonListener;
    }

    public void setOnDeletePaymentListener(Consumer<Payment> onDeletePaymentListener) {
        this.onDeletePaymentListener = onDeletePaymentListener;
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

    private void onClickPayment(int paymentIndex) {
        if (this.previousPayments == null) return;
        if (paymentIndex >= 0 && paymentIndex < this.previousPayments.size()) {
            final Payment p = previousPayments.get(paymentIndex);
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "¿Eliminar permanentemente este pago?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                if (onDeletePaymentListener != null) {
                    onDeletePaymentListener.accept(p);
                }
            }
        }
    }

    public void setPreviousPayments(List<Payment> payments) {
        List<String> paymentsItem = new ArrayList<>();
        for (Payment payment : payments) {
            paymentsItem.add(String.format("El %s ha pagado %s por $%d",
                    payment.getEmitDate().format(Utils.DATE_FORMATTER),
                    Utils.getMonthWithYear(payment.getMonthDate()),
                    payment.getMoney()));
        }
        paymentList.setListData(paymentsItem.toArray());
        this.previousPayments = payments;
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