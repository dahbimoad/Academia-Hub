package com.academiahub.schoolmanagement.Controllers.Sec;

import com.academiahub.schoolmanagement.DAO.ModuleDAO;
import com.academiahub.schoolmanagement.Models.Module;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleController {

    @FXML private TableView<Module> moduleTable;
    @FXML private TableColumn<Module, Integer> idColumn;
    @FXML private TableColumn<Module, String> nameColumn;
    @FXML private TableColumn<Module, String> descriptionColumn;
    @FXML private TableColumn<Module, Void> actionsColumn;
    @FXML private TextField searchField;

    private ObservableList<Module> moduleList;

    private ModuleDAO moduleDAO;

    public ModuleController() {
        // Initialisation du DAO (Assurez-vous d'initialiser la connexion correctement)
        moduleDAO = new ModuleDAO();
    }

    @FXML
    private void initialize() {
        // Configurer les colonnes de la table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Ajouter les boutons Modifier et Supprimer désactivés
        actionsColumn.setCellFactory(new ActionsCellFactory());

        // Charger les données des modules
        loadModules();
    }

    private void loadModules() {
        List<Module> modules = moduleDAO.getAllModules();
        moduleList = FXCollections.observableArrayList(modules);
        moduleTable.setItems(moduleList);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            moduleTable.setItems(moduleList);
        } else {
            ObservableList<Module> filteredList = FXCollections.observableArrayList(
                    moduleList.stream()
                            .filter(module -> module.getName().toLowerCase().contains(keyword) ||
                                    module.getDescription().toLowerCase().contains(keyword))
                            .collect(Collectors.toList())
            );
            moduleTable.setItems(filteredList);
        }
    }

    @FXML
    private void handleExport(ActionEvent event) {
        List<Module> modulesToExport = moduleTable.getItems();
        try (FileWriter writer = new FileWriter("modules_export.csv")) {
            writer.append("ID,Nom du Module,Description\n");
            for (Module module : modulesToExport) {
                writer.append(String.format("%d,\"%s\",\"%s\"\n",
                        module.getId(),
                        module.getName().replace("\"", "\"\""),
                        module.getDescription().replace("\"", "\"\"")));
            }
            showInformation("Export réussi", "Les modules ont été exportés avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'exportation des modules: " + e.getMessage());
        }
    }

    // Méthode pour afficher des messages d'information
    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher des messages d'erreur
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Classe interne pour créer les cellules d'actions
    public static class ActionsCellFactory implements Callback<TableColumn<Module, Void>, TableCell<Module, Void>> {

        @Override
        public TableCell<Module, Void> call(final TableColumn<Module, Void> param) {
            return new TableCell<Module, Void>() {
                private final Button editButton = new Button("Modifier");
                private final Button deleteButton = new Button("Supprimer");

                {
                    // Désactiver les boutons
                    editButton.setDisable(true);
                    deleteButton.setDisable(true);

                    // Optionnel: Ajouter des tooltips pour indiquer que les fonctionnalités ne sont pas disponibles
                    editButton.setTooltip(new Tooltip("Modification désactivée"));
                    deleteButton.setTooltip(new Tooltip("Suppression désactivée"));
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox container = new HBox(5, editButton, deleteButton);
                        setGraphic(container);
                    }
                }
            };
        }
    }
}
