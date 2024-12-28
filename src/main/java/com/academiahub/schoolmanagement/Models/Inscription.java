package com.academiahub.schoolmanagement.Models;

import java.util.Date;

public class Inscription {
    private int id;
    private Etudiant etudiant;
    private Module module;
    private Date dateInscription;

    public Inscription() {}

    public Inscription(Etudiant etudiant, Module module, Date dateInscription) {
        this.etudiant = etudiant;
        this.module = module;
        this.dateInscription = dateInscription;
    }

    // Getters
    public int getId() { return id; }
    public Etudiant getEtudiant() { return etudiant; }
    public Module getModule() { return module; }
    public Date getDateInscription() { return dateInscription; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
    public void setModule(Module module) { this.module = module; }
    public void setDateInscription(Date dateInscription) { this.dateInscription = dateInscription; }
}
