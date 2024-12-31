package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Etudiant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {
    private Connection connection;

    public EtudiantDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Etudiant> getAllEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT * FROM etudiants";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Etudiant etudiant = new Etudiant();
                etudiant.setId(resultSet.getInt("id"));
                etudiant.setMatricule(resultSet.getString("matricule"));
                etudiant.setNom(resultSet.getString("nom"));
                etudiant.setPrenom(resultSet.getString("prenom"));
                etudiant.setDateNaissance(resultSet.getDate("date_naissance"));
                etudiant.setEmail(resultSet.getString("email"));
                etudiant.setPromotion(resultSet.getString("promotion"));
                etudiants.add(etudiant);
            }
        }
        return etudiants;
    }

    public void addEtudiant(Etudiant etudiant) throws SQLException {
        String query = "INSERT INTO etudiants (matricule, nom, prenom, date_naissance, email, promotion) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, etudiant.getMatricule());
            preparedStatement.setString(2, etudiant.getNom());
            preparedStatement.setString(3, etudiant.getPrenom());
            preparedStatement.setDate(4, etudiant.getDateNaissance());
            preparedStatement.setString(5, etudiant.getEmail());
            preparedStatement.setString(6, etudiant.getPromotion());
            preparedStatement.executeUpdate();
        }
    }

    public void updateEtudiant(Etudiant etudiant) throws SQLException {
        String query = "UPDATE etudiants SET matricule = ?, nom = ?, prenom = ?, date_naissance = ?, email = ?, promotion = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, etudiant.getMatricule());
            preparedStatement.setString(2, etudiant.getNom());
            preparedStatement.setString(3, etudiant.getPrenom());
            preparedStatement.setDate(4, new java.sql.Date(etudiant.getDateNaissance().getTime()));
            preparedStatement.setString(5, etudiant.getEmail());
            preparedStatement.setString(6, etudiant.getPromotion());
            preparedStatement.setInt(7, etudiant.getId());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteEtudiant(int id) throws SQLException {
        String query = "DELETE FROM etudiants WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
