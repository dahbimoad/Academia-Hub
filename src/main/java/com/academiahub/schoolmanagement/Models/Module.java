package com.academiahub.schoolmanagement.Models;

import java.util.HashSet;
import java.util.Set;

public class Module {
    private int id;
    private String nomModule;
    private String codeModule;
    private Professeur professeur;
    private Set<Inscription> inscriptions;

    public Module() {
        this.inscriptions = new HashSet<>();
    }

    public Module(String nomModule, String codeModule, Professeur professeur) {
        this();
        this.nomModule = nomModule;
        this.codeModule = codeModule;
        this.professeur = professeur;
    }

    // Getters
    public int getId() { return id; }
    public String getNomModule() { return nomModule; }
    public String getCodeModule() { return codeModule; }
    public Professeur getProfesseur() { return professeur; }
    public Set<Inscription> getInscriptions() { return inscriptions; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNomModule(String nomModule) { this.nomModule = nomModule; }
    public void setCodeModule(String codeModule) { this.codeModule = codeModule; }
    public void setProfesseur(Professeur professeur) { this.professeur = professeur; }
    public void setInscriptions(Set<Inscription> inscriptions) { this.inscriptions = inscriptions; }
}
