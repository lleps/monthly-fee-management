package com.lleps.mfm.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Leandro B. on 01/11/2015.
 */
public class Category {
    private final String name;
    private int monthPrice;
    private List<Client> clients;
    private List<Payment> payments;

    public Category(String name, int monthPrice, List<Client> clients, List<Payment> payments) {
        this.name = name;
        this.monthPrice = monthPrice;
        this.clients = clients;
        this.payments = payments;
    }

    public String getName() {
        return name;
    }

    public void setMonthPrice(int monthPrice) {
        this.monthPrice = monthPrice;
    }

    public int getMonthPrice() {
        return monthPrice;
    }

    public void addClient(Client client) {
        clients.add(client);
        sortClientsByInscription();
    }

    public void removeClient(Client client) {
        clients.remove(client);
        sortClientsByInscription();
    }

    private void sortClientsByInscription() {
        clients = clients.stream()
                .sorted((c1, c2) -> (c2.getId() - c1.getId()))
                .collect(Collectors.toList());
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    @Override
    public String toString() {
        return name;
    }
}