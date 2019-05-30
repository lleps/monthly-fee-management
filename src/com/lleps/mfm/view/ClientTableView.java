package com.lleps.mfm.view;

import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.Payment;
import com.lleps.mfm.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.List;

/**
 * @author leandro on 25/10/15.
 */
public class ClientTableView extends JTable {
    public interface ClientRightClickListener {
        void onClientRightClick(Client client, JTable table, Point position);
    }

    private String[] filteringWords;

    private final static int COLUMN_CLIENT = 0;
    private final static int COLUMN_FIRSTNAME = 1;
    private final static int COLUMN_LASTNAME = 2;
    private final static int COLUMN_DNI = 3;
    private final static int COLUMN_INSCRIPTIONDATE = 4;
    private final static int COLUMN_HOMEADDRESS = 5;
    private final static int COLUMN_MAIL = 6;
    private final static int COLUMN_NEXTPAYMENT = 7;

    private final static Color ROWCOLOR_OUTDATED = new Color(0xFFCDCD);
    private final static Color ROWCOLOR_INACTIVE = new Color(100, 100, 100);
    private final static Color ROWCOLOR_WELL = new Color(240, 240, 240);
    private final static Color ROWCOLOR_SELECTED = new Color(0x3F10FA);

    private ClientRightClickListener clientRightClickListener;
    private boolean inactiveClientsVisible;

    public ClientTableView() {
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (getSelectedRow() == row) {
                    c.setBackground(ROWCOLOR_SELECTED);
                    c.setForeground(Color.WHITE);
                    return c;
                }

                Client client = getClientByRow(row);

                if (client != null && client.isInactive()) {
                    c.setBackground(ROWCOLOR_INACTIVE);
                    c.setForeground(Color.WHITE);
                } else if (((String) getValueAt(row, COLUMN_NEXTPAYMENT)).contains("Hace")) {
                    c.setBackground(ROWCOLOR_OUTDATED);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(ROWCOLOR_WELL);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row > -1) {
                    setRowSelectionInterval(row, row);
                    if (SwingUtilities.isRightMouseButton(e)) {
                        Client client = getClientByRow(row);
                        if (client != null && clientRightClickListener != null) {
                            clientRightClickListener.onClientRightClick(client, ClientTableView.this, getMousePosition());
                        }
                    }
                }
            }
        });

        setColumnSelectionAllowed(false);
    }

    public void setClientRightClickListener(ClientRightClickListener clientRightClickListener) {
        this.clientRightClickListener = clientRightClickListener;
    }

    private Client getClientByRow(int row) {
        try {
            return (Client) getValueAt(row, COLUMN_CLIENT);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public void setInactiveClientsVisible(boolean inactiveClientsVisible) {
        this.inactiveClientsVisible = inactiveClientsVisible;
        updateFilter();
    }

    public void updateFilter() {
        if (getRowSorter() != null) {
            getRowSorter().modelStructureChanged();
        }
    }

    public boolean isInactiveClientsVisible() {
        return inactiveClientsVisible;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void filter(String[] words) {
        this.filteringWords = words;
        updateFilter();
    }

    public void update(List<Client> clientSet, List<Payment> payments) {
        Object[] columnNames = new Object[] {
                "#Cliente",
                "Nombre",
                "Apellido",
                "DNI",
                "Inscripción",
                "Domicilio",
                "Correo electrónico",
                "Próxima cuota"
        };

        Map<Integer, List<Payment>> paymentsByIds = new HashMap<>(clientSet.size());
        for (Payment payment : payments) {
            paymentsByIds.getOrDefault(payment.getClientId(), new ArrayList<>()).add(payment);

            List<Payment> idPayments = paymentsByIds.get(payment.getClientId());
            if (idPayments == null) {
                idPayments = new ArrayList<>();
                paymentsByIds.put(payment.getClientId(), idPayments);
            }
            idPayments.add(payment);
        }

        Object[][] data = new Object[clientSet.size()][columnNames.length];
        int i = 0;
        for (Client client : clientSet) {
            data[i][COLUMN_CLIENT] = client;
            data[i][COLUMN_FIRSTNAME] = client.getFirstName();
            data[i][COLUMN_LASTNAME] = client.getLastName();
            data[i][COLUMN_DNI] = client.getDni();
            data[i][COLUMN_INSCRIPTIONDATE] = client.getInscriptionDate().format(Utils.DATE_FORMATTER);
            data[i][COLUMN_HOMEADDRESS] = client.getHomeAddress();
            data[i][COLUMN_MAIL] = client.getMail();

            String finalString = "Nunca";
            Optional<Payment> lastPayment = getClientLastPayment(client, paymentsByIds);
            if (lastPayment.isPresent()) {
                LocalDate nextMonthToPay = lastPayment.get().getMonthDate().plusMonths(1);
                Period periodBetweenNowAndNextMonthToPay = Period.between(LocalDate.now(), nextMonthToPay);
                finalString = readablePeriod(periodBetweenNowAndNextMonthToPay);
            }
            data[i][COLUMN_NEXTPAYMENT] = finalString;
            i++;
        }
        TableModel tableModel = new DefaultTableModel(data, columnNames);
        setModel(tableModel);

        RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
            @Override
            public boolean include(Entry entry) {
                Client client = (Client)entry.getValue(COLUMN_CLIENT);
                if (!inactiveClientsVisible && client.isInactive()) {
                    return false;
                }

                if (filteringWords == null) {
                    return true;
                }

                for (String word : filteringWords) {
                    if (!entryContainsWord(entry, word)) {
                        return false;
                    }
                }
                return true;
            }
        };

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setRowFilter(filter);
        setRowSorter(sorter);
    }

    private Optional<Payment> getClientLastPayment(Client client, Map<Integer, List<Payment>> paymentsById) {
        return paymentsById
                .getOrDefault(client.getId(), new ArrayList<>())
                .stream()
                .min((p1, p2) -> (int) (p2.getMonthDate().toEpochDay() - p1.getMonthDate().toEpochDay()));
    }

    private String readablePeriod(Period period) {
        if (period.isZero()) return "Hoy";
        String startsWith = period.isNegative() ? "Hace " : "En ";
        long months = Math.abs(period.toTotalMonths());
        int days = Math.abs(period.getDays());
        if (Math.abs(months) > 0) {
            String endsWith = (months > 1) ? "es" : "";
            return startsWith + months + " mes" + endsWith;
        } else {
            String endsWith = (days > 1) ? "s" : "";
            return startsWith + days + " dia" + endsWith;
        }
    }

    private boolean entryContainsWord(RowFilter.Entry entry, String word) {
        for (int i = 0; i < entry.getValueCount(); i++) {
            Object entryValue = entry.getValue(i);
            if (entryValue != null && Utils.containsIgnoreCase(entry.getValue(i).toString(), word)) {
                return true;
            }
        }
        return false;
    }
}