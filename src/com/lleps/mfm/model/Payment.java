package com.lleps.mfm.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author leandro on 23/10/15.
 */
public class Payment implements Serializable {

    private static final long serialVersionUID = 71823917239812L;

    private int clientId;
    private int money;
    private LocalDate monthDate;
    private LocalDate emitDate;

    public Payment(int clientId, int money, LocalDate monthDate, LocalDate emitDate) {
        this.clientId = clientId;
        this.money = money;
        this.monthDate = monthDate;
        this.emitDate = emitDate;
    }

    public int getClientId() {
        return clientId;
    }

    public int getMoney() {
        return money;
    }

    public LocalDate getMonthDate() {
        return monthDate;
    }

    public LocalDate getEmitDate() {
        return emitDate;
    }

    @Override
    public String toString() {
        return "$" + money;
    }
}