package com.lleps.mfm;

import com.lleps.mfm.view.*;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.Payment;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Leandro B. on 01/11/2015.
 */
public class CategoryController {
    CategoryView view;
    Category category;
    LocalDate currentRecaudationsMonth;

    public CategoryController(Category category) {
        this.category = category;

        view = new CategoryView(category.getName());
        view.setAddClientButtonListener(e -> showAddClientDialog());
        view.getTable().setClientRightClickListener((client, table, position) -> {
            JPopupMenu popupMenu = new LabeledPopupMenu(client.getFirstName() + " " + client.getLastName());
            clientRightClicked(popupMenu, client);
            popupMenu.show(table, (int)position.getX(), (int)position.getY());
        });
        view.setShowInactivesListener(e -> {
            boolean show = view.isShowInactivesMarked();
            view.getTable().setInactiveClientsVisible(show);
            saveSettings();
        });
        view.setMonthMoney(category.getMonthPrice());
        view.setMonthMoneyChangeListener(e -> {
            int newMoney = view.getMonthMoney();
            category.setMonthPrice(newMoney);
            saveSettings();
        });
        view.setBrowseStringChangeListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                view.getTable().filter(view.getBrowseString().split(" "));
            }
        });
        view.setRecaudationsListener(e -> showRecaudations());
        view.setPlansListener(e -> {
            CategoryPlansView plansView = new CategoryPlansView(category);
            plansView.setLocationRelativeTo(view);
            plansView.setVisible(true);
        });
        view.getTable().update(category.getClients(), category.getPayments());
    }

    private void showRecaudations() {
        currentRecaudationsMonth = LocalDate.now();
        CategoryPaymentsView categoryPaymentsView = new CategoryPaymentsView();
        updateCategoryPaymentsView(categoryPaymentsView);
        categoryPaymentsView.setExtendedState(JFrame.MAXIMIZED_BOTH);
        categoryPaymentsView.setNextListener(e -> {
            currentRecaudationsMonth = currentRecaudationsMonth.plusDays(categoryPaymentsView.getMonthCount());
            updateCategoryPaymentsView(categoryPaymentsView);
        });
        categoryPaymentsView.setPreviousListener(e -> {
            currentRecaudationsMonth = currentRecaudationsMonth.minusDays(categoryPaymentsView.getMonthCount());
            updateCategoryPaymentsView(categoryPaymentsView);
        });
        categoryPaymentsView.setTitle("Pagos de " + category.getName());
        categoryPaymentsView.setVisible(true);

    }

    private void updateCategoryPaymentsView(CategoryPaymentsView categoryPaymentsView) {
        for (int i = 0; i < categoryPaymentsView.getMonthCount(); i++) {
            setPaymentsViewMonth(categoryPaymentsView, i, currentRecaudationsMonth.minusDays(i));
        }
    }

    private void setPaymentsViewMonth(CategoryPaymentsView categoryView, int index, LocalDate month) {
        categoryView.getMonthPaymentView(index).setMonth(month);
        categoryView.getMonthPaymentView(index).setPayments(category.getPayments().stream()
                .filter(m -> m.getEmitDate().equals(month))
                .collect(Collectors.toList()));
    }

    private void clientRightClicked(JPopupMenu menu, Client client) {
        JMenuItem paymentsItem = new JMenuItem("Pagar o ver cuotas", Resources.getInstance().PAYMENT_ICON);
        paymentsItem.addActionListener(e -> showPaymentsDialog(client));
        menu.add(paymentsItem);

        JMenuItem inactivityItem = new JMenuItem(
                (client.isInactive() ? "Desmarcar" : "Marcar") + " como inactivo", Resources.getInstance().BAN_ICON);
        inactivityItem.addActionListener(e -> {
            client.setInactive(!client.isInactive());
            tableChanged();
            saveClients();
        });
        menu.add(inactivityItem);

        JMenuItem editItem = new JMenuItem("Editar", Resources.getInstance().PENCIL_ICON);
        editItem.addActionListener(e -> {
            showEditClientDialog(client);
        });
        menu.add(editItem);

        JMenuItem exercisesItem = new JMenuItem("Planes", Resources.getInstance().PLUS_ICON);
        exercisesItem.addActionListener(e -> {
            ClientExercisePlansView dialog = new ClientExercisePlansView(category, client);
            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);
        });
        menu.add(exercisesItem);

        JMenuItem deleteItem = new JMenuItem("Eliminar", Resources.getInstance().TRASH_ICON);
        deleteItem.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    view,
                    "¿Eliminar permanentemente a " + client.getFirstName() + " " + client.getLastName() + " " +
                    "y todos sus pagos?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                removeClientAndTheirPayments(client);
            }
        });
        menu.add(deleteItem);
    }


    private void showEditClientDialog(Client client) {
        ClientEditView cev = new ClientEditView();
        cev.setTitle("Editar a " + client.getFirstName() + " " + client.getLastName());
        cev.setNameField(client.getFirstName());
        cev.setLastNameField(client.getLastName());
        cev.setHomeAddressField(client.getHomeAddress());
        cev.setDniField(Integer.toString(client.getDni()));
        cev.setPhoneNumberField(client.getPhoneNumber());
        cev.setMailField(client.getMail());
        cev.setObservationsField(client.getObservations());
        cev.setMaleSelected(client.isMale());
        cev.setAcceptButtonListener(e -> {
            client.setFirstName(cev.getNameField());
            client.setLastName(cev.getLastNameField());
            client.setHomeAddress(cev.getHomeAddressField());
            client.setPhoneNumber(cev.getPhoneNumberField());
            client.setDni(Integer.parseInt(cev.getDniField()));
            client.setMail(cev.getMailField());
            client.setObservations(cev.getObservationsField());
            client.setMale(cev.isMaleSelected());
            cev.dispose();
            updateTable();
        });
        cev.setCancelButtonListener(e -> cev.dispose());
        cev.setLocationRelativeTo(view);
        cev.setVisible(true);
    }

    private void showPaymentsDialog(Client client) {
        ClientPaymentsView paymentsView = new ClientPaymentsView();
        paymentsView.setTitle("Pagos de " + client.getFirstName() + " " + client.getLastName());
        paymentsView.setPreviousPayments(category.getPayments()
                .stream()
                .filter(p -> p.getClientId() == client.getId())
                .collect(Collectors.toList()));

        paymentsView.setAmountField(category.getMonthPrice());
        paymentsView.setObservations(client.getObservations());

        // preselect next payment month
        Optional<Payment> lastPaymentOpt = getClientLastPayment(client);
        if (lastPaymentOpt.isPresent()) {
            paymentsView.setSelectedMonth(lastPaymentOpt.get().getMonthDate().plusMonths(1));
        } else {
            paymentsView.setSelectedMonth(LocalDate.now());
        }

        paymentsView.setAcceptButtonListener(e -> {
            int priceToPay = paymentsView.getAmountField().orElse(category.getMonthPrice());
            Payment payment = new Payment(client.getId(), priceToPay,
                    paymentsView.getSelectedMonth(), LocalDate.now());
            if (!client.getObservations().equals(paymentsView.getObservations())) {
                client.setObservations(paymentsView.getObservations());
                saveClients();
            }

            paymentsView.dispose();
            addPayment(payment);
            System.out.println("add new payment: " + payment);
        });

        paymentsView.setCancelButtonListener(e -> paymentsView.dispose());
        paymentsView.setOnDeletePaymentListener(payment -> {
            removePayment(payment);
            paymentsView.dispose();
            SwingUtilities.invokeLater(() -> {
                showPaymentsDialog(client);
            });
        });
        paymentsView.setLocationRelativeTo(view);
        paymentsView.setVisible(true);
    }

    private Optional<Payment> getClientLastPayment(Client client) {
        return category.getPayments().stream()
                .filter(p -> p.getClientId() == client.getId())
                .sorted((p1, p2) -> (int)(p2.getMonthDate().toEpochDay() - p1.getMonthDate().toEpochDay()))
                .findFirst();
    }

    private List<LocalDate> getPreviousMonthsSince(LocalDate monthInclusive, int count) {
        List<LocalDate> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(monthInclusive.minusMonths(i));
        }
        return result;
    }



    private void showAddClientDialog() {
        ClientEditView clientView = new ClientEditView();
        clientView.setTitle("Agregar cliente");
        clientView.setAcceptButtonListener(e -> {
            int dni = 0;
            try {
                dni = Integer.parseInt(clientView.getDniField());
            } catch (Exception exception) { /**/ }

            if (dni == 0) {
                JOptionPane.showMessageDialog(view, "El DNI escrito no es válido.", "DNI Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int finalDni = dni;
            if (category.getClients().stream().anyMatch(c -> c.getDni() == finalDni)) {
                JOptionPane.showMessageDialog(view, "El DNI escrito ya está registrado.", "DNI en uso", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Client client = new Client(getFreeClientId(), clientView.isMaleSelected(), dni, clientView.getNameField(),
                    clientView.getLastNameField(), clientView.getPhoneNumberField(), clientView.getHomeAddressField(),
                    clientView.getMailField(), LocalDate.now(), clientView.getObservationsField(), new ArrayList<>());
            clientView.dispose();
            addClient(client);
            showPaymentsDialog(client);
        });
        clientView.setCancelButtonListener(e -> clientView.dispose());
        clientView.setLocationRelativeTo(view);
        clientView.setVisible(true);
    }

    private int getFreeClientId() {
        return category.getClients().stream().mapToInt(Client::getId).max().orElse(0) + 1;
    }

    private void addClient(Client client) {
        category.addClient(client);
        updateTable();
        saveClients();
    }

    private void removeClientAndTheirPayments(Client client) {
        category.getPayments()
                .stream()
                .filter(p -> p.getClientId() == client.getId())
                .collect(Collectors.toList())
                .forEach(p -> category.removePayment(p));
        category.removeClient(client);
        updateTable();
        saveClients();
        savePayments();
    }

    private void addPayment(Payment payment) {
        category.addPayment(payment);
        updateTable();
        savePayments();
    }

    private void removePayment(Payment payment) {
        category.removePayment(payment);
        updateTable();
        savePayments();
    }

    private void updateTable() {
        FloatingMessageView.show("Actualizando...");
        view.getTable().update(category.getClients(), category.getPayments());
        FloatingMessageView.hide();
    }

    public CategoryView getView() {
        return view;
    }

    public Category getCategory() {
        return category;
    }

    private void saveClients() {
        FloatingMessageView.show("Guardando...");
        try {
            Storage.getInstance().saveCategoryClients(category);
        } catch (Exception e) {
            Utils.reportException(e, "No se pudieron guardar los clientes");
        }
        FloatingMessageView.hide();
    }

    private void saveSettings() {
        try {
            Storage.getInstance().saveCategoryConfig(category);
        } catch (Exception e) {
            Utils.reportException(e, "No se pudo guardar la configuraci�n");
        }
    }

    private void savePayments() {
        FloatingMessageView.show("Guardando...");
        try {
            Storage.getInstance().saveCategoryPayments(category);
        } catch (Exception e) {
            Utils.reportException(e, "No se pudieron guardar los pagos");
        }
        FloatingMessageView.hide();
    }

    private void tableChanged() {
        view.getTable().updateFilter();
    }
}