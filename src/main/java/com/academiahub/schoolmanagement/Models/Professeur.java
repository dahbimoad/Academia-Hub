package com.academiahub.schoolmanagement.Models;

import java.util.HashSet;
import java.util.Set;

public class Professeur {
    private int id;
    private String nom;
    private String prenom;
    private String specialite;
    private int userId;
    private Set<Module> modules;

    public Professeur() {
        this.modules = new HashSet<>();
    }

    public Professeur(String nom, String prenom, String specialite) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
    }

    // Getters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getSpecialite() { return specialite; }
    public Set<Module> getModules() { return modules; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public void setModules(Set<Module> modules) { this.modules = modules; }
}

