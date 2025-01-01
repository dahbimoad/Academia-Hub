package com.academiahub.schoolmanagement.DAO;
import com.academiahub.schoolmanagement.Models.Module;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModuleDOA {
    private Connection connection;
    public ModuleDOA(Connection connection) {
        this.connection = connection;
    }
    public List<Module> getAllModules() throws SQLException {
        List<Module> modules = new ArrayList<>();
        String query = "SELECT id, nom_module FROM modules";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Module module = new Module();
                module.setId(resultSet.getInt("id"));
                module.setNomModule(resultSet.getString("nom_module"));
                modules.add(module);
            }
        }
        return modules;
    }

}
