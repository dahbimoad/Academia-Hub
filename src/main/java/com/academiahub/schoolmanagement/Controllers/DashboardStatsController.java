package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardStatsController {
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalProfessorsLabel;
    @FXML private Label totalModulesLabel;
    @FXML private BarChart<String, Number> popularModulesChart;
    @FXML private PieChart professorsWorkloadChart;

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load summary statistics
            loadSummaryStats(conn);

            // Load charts data
            loadPopularModulesChart(conn);
            loadProfessorsWorkloadChart(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error appropriately
        }
    }

    private void loadSummaryStats(Connection conn) throws SQLException {
        // Count total students
        String studentQuery = "SELECT COUNT(*) FROM etudiants";
        try (PreparedStatement stmt = conn.prepareStatement(studentQuery)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalStudentsLabel.setText(String.valueOf(rs.getInt(1)));
            }
        }

        // Count total professors
        String profQuery = "SELECT COUNT(*) FROM professeurs";
        try (PreparedStatement stmt = conn.prepareStatement(profQuery)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalProfessorsLabel.setText(String.valueOf(rs.getInt(1)));
            }
        }

        // Count total modules
        String moduleQuery = "SELECT COUNT(*) FROM modules";
        try (PreparedStatement stmt = conn.prepareStatement(moduleQuery)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalModulesLabel.setText(String.valueOf(rs.getInt(1)));
            }
        }
    }

    private void loadPopularModulesChart(Connection conn) throws SQLException {
        String query = "SELECT m.nom_module, COUNT(i.etudiant_id) as student_count " +
                      "FROM modules m " +
                      "LEFT JOIN inscriptions i ON m.id = i.module_id " +
                      "GROUP BY m.id, m.nom_module " +
                      "ORDER BY student_count DESC " +
                      "LIMIT 5";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'étudiants");

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                    rs.getString("nom_module"),
                    rs.getInt("student_count")
                ));
            }
        }

        popularModulesChart.getData().clear();
        popularModulesChart.getData().add(series);
    }

    private void loadProfessorsWorkloadChart(Connection conn) throws SQLException {
        String query = "SELECT p.nom, p.prenom, COUNT(m.id) as module_count " +
                      "FROM professeurs p " +
                      "LEFT JOIN modules m ON p.id = m.professeur_id " +
                      "GROUP BY p.id, p.nom, p.prenom " +
                      "ORDER BY module_count DESC " +
                      "LIMIT 5";

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String profName = rs.getString("nom") + " " + rs.getString("prenom");
                int moduleCount = rs.getInt("module_count");
                pieChartData.add(new PieChart.Data(profName, moduleCount));
            }
        }

        professorsWorkloadChart.setData(pieChartData);
    }
}