//src/main/java/com/academiahub/schoolmanagement/Models/Utilisateur.java
package com.academiahub.schoolmanagement.Models;

public class Utilisateur {
    private int id;
    private String username;
    private String password;
    private String role;

    public Utilisateur() {}

    public Utilisateur(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) {
        if (role != null && (role.equals("ADMIN") || role.equals("SECRETAIRE") || role.equals("PROFESSEUR"))) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Invalid role. Must be ADMIN, SECRETAIRE, or PROFESSEUR");
        }
    }
}
