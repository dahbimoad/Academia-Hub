package com.academiahub.schoolmanagement.Models;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class Etudiant {
    private int id;
    private String matricule;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String email;
    private String promotion;
    private Set<Inscription> inscriptions;

    public Etudiant() {
        this.inscriptions = new HashSet<>();
    }

    public Etudiant(String matricule, String nom, String prenom, Date dateNaissance, String email, String promotion) {
        this();
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.promotion = promotion;
    }



    // Getters
    public int getId() { return id; }
    public String getMatricule() { return matricule; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public Date getDateNaissance() { return dateNaissance; }
    public String getEmail() { return email; }
    public String getPromotion() { return promotion; }
    public Set<Inscription> getInscriptions() { return inscriptions; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setEmail(String email) { this.email = email; }
    public void setPromotion(String promotion) { this.promotion = promotion; }
    public void setInscriptions(Set<Inscription> inscriptions) { this.inscriptions = inscriptions; }

    public String getName() {
        return nom;
    }
}
