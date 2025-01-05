package com.academiahub.schoolmanagement.Controllers.Admin;

import com.academiahub.schoolmanagement.DAO.ModuleDAO;
import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.utils.AlertUtils;
import com.academiahub.schoolmanagement.utils.ExcelExporter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModuleController {

    private static final Logger LOGGER = Logger.getLogger(ModuleController.class.getName());

    @FXML private TableView<Module> moduleTable;
    @FXML private TableColumn<Module, Integer> colId;
    @FXML private TableColumn<Module, String> colNomModule;
    @FXML private TableColumn<Module, String> colCodeModule;
    @FXML private TableColumn<Module, String> colProfesseur;
    @FXML private TableColumn<Module, Integer> colNbEtudiants;

    @FXML private VBox formContainer;
    @FXML private TextField nomModuleField;
    @FXML private TextField codeModuleField;
    @FXML private ComboBox<String> professeurComboBox;
    @FXML private TextField searchField;

    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnValider;
    @FXML private Button btnAnnuler;
    @FXML private Button btnExporter;

    private final ModuleDAO moduleDAO = new ModuleDAO();
    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final ObservableList<Module> masterData = FXCollections.observableArrayList();
    private final ObservableList<Professeur> allProfesseurs = FXCollections.observableArrayList();
    private FilteredList<Module> filteredData;
    private SortedList<Module> sortedData;

    private Mode currentMode = Mode.NORMAL;

    private enum Mode {
        NORMAL, AJOUT, MODIFICATION
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearch();
        loadProfesseurs();
        setupTableSelection();
        loadData();
        initializeFormVisibility();
    }

    private void initializeFormVisibility() {
        formContainer.setVisible(false);
        formContainer.setManaged(false);
        btnValider.setVisible(false);
        btnAnnuler.setVisible(false);
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNomModule.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomModule()));
        colCodeModule.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodeModule()));
        colProfesseur.setCellValueFactory(cellData ->
                cellData.getValue().getProfesseur() != null ?
                        new SimpleStringProperty(cellData.getValue().getProfesseur().getNom() + " " + cellData.getValue().getProfesseur().getPrenom()) :
                        new SimpleStringProperty(""));
        colNbEtudiants.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNbEtudiants()).asObject());
    }

    private void setupSearch() {
        filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(module -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return module.getNomModule().toLowerCase().contains(lowerCaseFilter) ||
                        module.getCodeModule().toLowerCase().contains(lowerCaseFilter) ||
                        (module.getProfesseur() != null &&
                                (module.getProfesseur().getNom().toLowerCase().contains(lowerCaseFilter) ||
                                        module.getProfesseur().getPrenom().toLowerCase().contains(lowerCaseFilter)));
            });
        });

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(moduleTable.comparatorProperty());
        moduleTable.setItems(sortedData);
    }

    private void loadProfesseurs() {
        try {
            List<Professeur> professeurs = professeurDAO.findAll();
            allProfesseurs.setAll(professeurs);
            ObservableList<String> professeurNames = professeurs.stream()
                    .map(p -> p.getNom() + " " + p.getPrenom())
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            professeurComboBox.setItems(professeurNames);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des professeurs.", e);
            AlertUtils.showError("Erreur", "Impossible de charger les professeurs.");
        }
    }

    private void setupTableSelection() {
        moduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (currentMode == Mode.NORMAL && newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void loadData() {
        try {
            Module selectedModule = moduleTable.getSelectionModel().getSelectedItem();
            masterData.clear();
            List<Module> modules = moduleDAO.findAll();
            masterData.addAll(modules);
            if (selectedModule != null) {
                for (Module module : masterData) {
                    if (module.getId() == selectedModule.getId()) {
                        moduleTable.getSelectionModel().select(module);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des modules.", e);
            AlertUtils.showError("Erreur", "Impossible de charger les modules.");
        }
    }

    @FXML
    private void handleAjouterButton() {
        currentMode = Mode.AJOUT;
        showForm(true);
        clearFields();
    }

    @FXML
    private void handleModifierButton() {
        Module selected = moduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Sélection requise", "Veuillez sélectionner un module à modifier.");
            return;
        }
        currentMode = Mode.MODIFICATION;
        showForm(true);
        populateFields(selected);
    }

    @FXML
    private void handleSupprimerButton() {
        Module selected = moduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Sélection requise", "Veuillez sélectionner un module à supprimer.");
            return;
        }

        Optional<ButtonType> confirmation = AlertUtils.showConfirmation("Confirmation de suppression", "Voulez-vous vraiment supprimer ce module ?");
        if (confirmation.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (moduleDAO.delete(selected.getId())) {
                    AlertUtils.showInformation("Succès", "Module supprimé avec succès.");
                    loadData();
                    clearFields();
                } else {
                    LOGGER.log(Level.SEVERE, "Échec de la suppression du module avec l'ID : " + selected.getId());
                    AlertUtils.showError("Erreur", "Erreur lors de la suppression du module.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Exception lors de la suppression du module avec l'ID : " + selected.getId(), e);
                AlertUtils.showError("Erreur", "Erreur lors de la suppression du module.");
            }
        }
    }

    @FXML
    private void handleValiderButton() {
        if (currentMode == Mode.AJOUT) {
            ajouterModule();
        } else if (currentMode == Mode.MODIFICATION) {
            modifierModule();
        }
        showForm(false);
    }

    @FXML
    private void handleAnnulerButton() {
        showForm(false);
        clearFields();
        currentMode = Mode.NORMAL;
    }

    @FXML
    private void handleExporterButton() {
        try {
            ExcelExporter.exportModules(masterData, ((Stage) btnExporter.getScene().getWindow()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'exportation des modules.", e);
            AlertUtils.showError("Erreur", "Impossible d'exporter les modules.");
        }
    }

    private void showForm(boolean show) {
        formContainer.setVisible(show);
        formContainer.setManaged(show);
        btnAjouter.setDisable(show);
        btnModifier.setDisable(show);
        btnSupprimer.setDisable(show);
        moduleTable.setDisable(show);
        btnValider.setVisible(show);
        btnAnnuler.setVisible(show);
    }

    private void ajouterModule() {
        if (!validateFields()) return;

        Module module = new Module();
        module.setNomModule(nomModuleField.getText().trim());
        module.setCodeModule(codeModuleField.getText().trim());
        module.setProfesseur(getSelectedProfesseur());

        try {
            if (moduleDAO.create(module)) {
                AlertUtils.showInformation("Succès", "Module ajouté avec succès.");
                loadData();
                clearFields();
            } else {
                LOGGER.log(Level.SEVERE, "Échec de l'ajout du module : " + module);
                AlertUtils.showError("Erreur", "Erreur lors de l'ajout du module.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception lors de l'ajout du module : " + module, e);
            AlertUtils.showError("Erreur", "Erreur lors de l'ajout du module.");
        }
    }

    private void modifierModule() {
        Module selected = moduleTable.getSelectionModel().getSelectedItem();
        if (selected == null || !validateFields()) return;

        selected.setNomModule(nomModuleField.getText().trim());
        selected.setCodeModule(codeModuleField.getText().trim());
        selected.setProfesseur(getSelectedProfesseur());

        try {
            if (moduleDAO.update(selected)) {
                AlertUtils.showInformation("Succès", "Module modifié avec succès.");
                loadData();
                clearFields();
            } else {
                LOGGER.log(Level.SEVERE, "Échec de la modification du module : " + selected);
                AlertUtils.showError("Erreur", "Erreur lors de la modification du module.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception lors de la modification du module : " + selected, e);
            AlertUtils.showError("Erreur", "Erreur lors de la modification du module.");
        }
    }

    private boolean validateFields() {
        String nomModule = nomModuleField.getText().trim();
        String codeModule = codeModuleField.getText().trim();
        String professeur = professeurComboBox.getValue();

        if (nomModule.isEmpty() || codeModule.isEmpty() || professeur == null) {
            AlertUtils.showWarning("Champs obligatoires", "Veuillez remplir tous les champs.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        nomModuleField.clear();
        codeModuleField.clear();
        professeurComboBox.getSelectionModel().clearSelection();
        moduleTable.getSelectionModel().clearSelection();
    }

    private void populateFields(Module module) {
        nomModuleField.setText(module.getNomModule());
        codeModuleField.setText(module.getCodeModule());
        if (module.getProfesseur() != null) {
            professeurComboBox.setValue(module.getProfesseur().getNom() + " " + module.getProfesseur().getPrenom());
        } else {
            professeurComboBox.setValue(null);
        }
    }

    private Professeur getSelectedProfesseur() {
        String selectedProfesseurName = professeurComboBox.getValue();
        if (selectedProfesseurName == null) return null;

        return allProfesseurs.stream()
                .filter(p -> (p.getNom() + " " + p.getPrenom()).equals(selectedProfesseurName))
                .findFirst()
                .orElse(null);
    }

    @FXML
    private void actualiserTable() {
        loadData();
    }
}
