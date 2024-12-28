package com.academiahub.schoolmanagement.Models;

public class Professeur {
    private int id;
    private String nom;
    private String prenom;
    private String specialite;
    private int userId; // Nouveau champ ajouté

    public Professeur() {}

    public Professeur(int id, String nom, String prenom, String specialite, int userId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.userId = userId; // Initialisation du nouveau champ
    }

    // Getters et setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSpecialite() {
        return specialite;
    }
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public int getUserId() {
        return userId; // Getter pour userId
    }
    public void setUserId(int userId) {
        this.userId = userId; // Setter pour userId
    }

    @Override
    public String toString() {
        return "Professeur [id=" + id + ", nom=" + nom + ", prenom=" + prenom
                + ", specialite=" + specialite + ", userId=" + userId + "]"; // Ajout de userId dans toString
    }
}
