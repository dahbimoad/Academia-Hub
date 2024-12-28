package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Professeur;


import java.sql.*;
import java.util.*;

public class ProfesseurDAO {
    private Connection connection;

    public ProfesseurDAO(Connection connection) {
        this.connection = connection;
    }

    // Get professor by user_id
    public Professeur getProfesseurByUsername(String username) {
        String query = "SELECT p.* FROM professeurs p " +
                      "JOIN utilisateurs u ON p.user_id = u.id " +
                      "WHERE u.username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Professeur prof = new Professeur();
                prof.setId(rs.getInt("id"));
                prof.setNom(rs.getString("nom"));
                prof.setPrenom(rs.getString("prenom"));
                prof.setSpecialite(rs.getString("specialite"));
                prof.setUserId(rs.getInt("user_id"));
                return prof;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get modules for a professor using professor ID (not user_id)
    public List<Module> getProfesseurModules(String username) {
        List<Module> modules = new ArrayList<>();
        String query = "SELECT m.* FROM modules m " +
                      "JOIN professeurs p ON m.professeur_id = p.id " +
                      "JOIN utilisateurs u ON p.user_id = u.id " +
                      "WHERE u.username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Module module = new Module();
                module.setId(rs.getInt("id"));
                module.setNomModule(rs.getString("nom_module"));
                module.setCodeModule(rs.getString("code_module"));
                modules.add(module);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modules;
    }
    public List<Etudiant> getModuleStudents(int moduleId) {
        List<Etudiant> students = new ArrayList<>();
        String query = "SELECT e.* FROM etudiants e " +
                      "JOIN inscriptions i ON e.id = i.etudiant_id " +
                      "WHERE i.module_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, moduleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Etudiant student = new Etudiant();
                student.setId(rs.getInt("id"));
                student.setMatricule(rs.getString("matricule"));
                student.setNom(rs.getString("nom"));
                student.setPrenom(rs.getString("prenom"));
                student.setEmail(rs.getString("email"));
                student.setPromotion(rs.getString("promotion"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}