package com.academiahub.schoolmanagement.Models;

import java.util.Date;

public class Inscription {
    private int id;
    private int etudiantId; // ID of the student
    private String etudiantNom; // Name of the student
    private String etudiantPrenom; // First name of the student
    private int moduleId; // ID of the module
    private String moduleNom; // Name of the module
    private Date dateInscription; // Date of enrollment

    // Default constructor
    public Inscription() {}

    // Parameterized constructor
    public Inscription(int id, int etudiantId, String etudiantNom, String etudiantPrenom,
                       int moduleId, String moduleNom, Date dateInscription) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.etudiantNom = etudiantNom;
        this.etudiantPrenom = etudiantPrenom;
        this.moduleId = moduleId;
        this.moduleNom = moduleNom;
        this.dateInscription = dateInscription;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getEtudiantId() {
        return etudiantId;
    }

    public String getEtudiantNom() {
        return etudiantNom;
    }

    public String getEtudiantPrenom() {
        return etudiantPrenom;
    }

    public int getModuleId() {
        return moduleId;
    }

    public String getModuleNom() {
        return moduleNom;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEtudiantId(int etudiantId) {
        this.etudiantId = etudiantId;
    }

    public void setEtudiantNom(String etudiantNom) {
        this.etudiantNom = etudiantNom;
    }

    public void setEtudiantPrenom(String etudiantPrenom) {
        this.etudiantPrenom = etudiantPrenom;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public void setModuleNom(String moduleNom) {
        this.moduleNom = moduleNom;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    // Overriding toString for debugging
    @Override
    public String toString() {
        return "Inscription{" +
                "id=" + id +
                ", etudiantId=" + etudiantId +
                ", etudiantNom='" + etudiantNom + '\'' +
                ", etudiantPrenom='" + etudiantPrenom + '\'' +
                ", moduleId=" + moduleId +
                ", moduleNom='" + moduleNom + '\'' +
                ", dateInscription=" + dateInscription +
                '}';
    }

    public void setInscriptionId(int inscriptionId) {
        this.id = inscriptionId;
    }
}
