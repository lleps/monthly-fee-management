package com.lleps.mfm.view;

import com.lleps.mfm.model.Payment;
import com.lleps.mfm.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Leandro B. on 01/11/2015.
 */
public class MonthPaymentsView extends JPanel {
    private JTable table;
    private JLabel monthLabel;
    private JLabel moneyLabel;
    private JPanel mainPanel;

    public MonthPaymentsView() {
        super(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public void setMonth(LocalDate month) {
        monthLabel.setText(Utils.firstUpperCase(Utils.getDayWithMonth(month)));
    }

    public void setPayments(List<Payment> payments) {
        String header[] = {"#Cliente", "Monto", "Fecha"};
        Object[][] data = new Object[payments.size()][header.length];
        int i = 0, moneyCount = 0;
        for (Payment payment : payments) {
            data[i][0] = payment.getClientId();
            data[i][1] = payment.getMoney();
            data[i][2] = payment.getEmitDate().format(Utils.DATE_FORMATTER);
            moneyCount += payment.getMoney();
            i++;
        }
        table.setModel(new DefaultTableModel(data, header));
        moneyLabel.setText(Utils.priceToString(moneyCount));
    }

    private void createUIComponents() {
        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
