package com.lleps.mfm.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leandro on 17/10/15.
 */
public class Client implements Serializable {

    private static final long serialVersionUID = 821809380571L;

    private int id;
    private boolean male;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String homeAddress;
    private String mail;
    private LocalDate inscriptionDate;
    private String observations;
    private boolean inactive;
    private List<ExercisePlan> exercisePlans;

    public Client(int id,
                  boolean male,
                  String firstName,
                  String lastName,
                  String phoneNumber,
                  String homeAddress,
                  String mail,
                  LocalDate inscriptionDate,
                  String observations,
                  List<ExercisePlan> exercisePlans) {
        this.id = id;
        this.male = male;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.homeAddress = homeAddress;
        this.mail = mail;
        this.inscriptionDate = inscriptionDate;
        this.observations = observations;
        this.exercisePlans = exercisePlans;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public boolean isInactive() {
        return inactive;
    }

    public int getId() {
        return id;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isMale() {
        return male;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public LocalDate getInscriptionDate() {
        return inscriptionDate;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getObservations() {
        return observations;
    }

    public List<ExercisePlan> getExercisePlans() {
        return exercisePlans;
    }

    public void setExercisePlans(List<ExercisePlan> exercisePlans) {
        this.exercisePlans = exercisePlans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        return id == client.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "#" + Integer.toString(id);
    }
}
