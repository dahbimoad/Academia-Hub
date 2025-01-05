package com.academiahub.schoolmanagement.utils;

// Importations existantes
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Professeur;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilitaire pour l'exportation des données en Excel.
 */
public class ExcelExporter {
    private static final Logger LOGGER = Logger.getLogger(ExcelExporter.class.getName());

    /**
     * Exporte la liste des professeurs dans un fichier Excel.
     *
     * @param data   La liste des professeurs à exporter.
     * @param window La fenêtre parent pour le FileChooser.
     */
    public static void exportProfesseurs(ObservableList<Professeur> data, Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter vers Excel");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("professeurs_" + timestamp + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            exportToExcel(file, data, "Professeurs");
        }
    }

    /**
     * Exporte la liste des modules dans un fichier Excel.
     *
     * @param data   La liste des modules à exporter.
     * @param window La fenêtre parent pour le FileChooser.
     */
    public static void exportModules(ObservableList<Module> data, Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les Modules vers Excel");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("modules_" + timestamp + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            exportToExcel(file, data, "Modules");
        }
    }

    /**
     * Méthode générique pour exporter des données dans un fichier Excel.
     *
     * @param file  Le fichier de destination.
     * @param data  Les données à exporter.
     * @param sheetName Le nom de la feuille Excel.
     */
    private static <T> void exportToExcel(File file, ObservableList<T> data, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            createHeader(workbook, sheet, sheetName);
            fillData(sheet, data, sheetName);
            autoSizeColumns(sheet, sheetName);

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                AlertUtils.showInformation("Export réussi", "Export Excel réussi !\nFichier : " + file.getName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'export Excel", e);
            AlertUtils.showError("Erreur d'export", "Impossible d'exporter les données vers Excel.");
        }
    }

    /**
     * Crée l'en-tête de la feuille Excel en fonction du type de données.
     *
     * @param workbook  Le classeur Excel.
     * @param sheet     La feuille Excel.
     * @param sheetName Le nom de la feuille Excel.
     */
    private static <T> void createHeader(Workbook workbook, Sheet sheet, String sheetName) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] headers;

        if (sheetName.equals("Professeurs")) {
            headers = new String[]{"ID", "Nom", "Prénom", "Spécialité", "User ID"};
        } else if (sheetName.equals("Modules")) {
            headers = new String[]{"ID", "Nom Module", "Code Module", "Professeur", "Nombre d'Étudiants"};
        } else {
            headers = new String[]{};
        }

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Remplit les données dans la feuille Excel en fonction du type de données.
     *
     * @param sheet     La feuille Excel.
     * @param data      Les données à remplir.
     * @param sheetName Le nom de la feuille Excel.
     */
    private static <T> void fillData(Sheet sheet, ObservableList<T> data, String sheetName) {
        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (T item : data) {
            Row row = sheet.createRow(rowNum++);
            if (sheetName.equals("Professeurs") && item instanceof Professeur) {
                Professeur prof = (Professeur) item;
                row.createCell(0).setCellValue(prof.getId());
                row.createCell(1).setCellValue(prof.getNom());
                row.createCell(2).setCellValue(prof.getPrenom());
                row.createCell(3).setCellValue(prof.getSpecialite());
                row.createCell(4).setCellValue(prof.getUserId());
            } else if (sheetName.equals("Modules") && item instanceof Module) {
                Module module = (Module) item;
                row.createCell(0).setCellValue(module.getId());
                row.createCell(1).setCellValue(module.getNomModule());
                row.createCell(2).setCellValue(module.getCodeModule());
                row.createCell(3).setCellValue(module.getProfesseur() != null ?
                        module.getProfesseur().getNom() + " " + module.getProfesseur().getPrenom() : "N/A");
                row.createCell(4).setCellValue(module.getNbEtudiants());

            }
        }
    }

    /**
     * Ajuste automatiquement la largeur des colonnes.
     *
     * @param sheet     La feuille Excel.
     * @param sheetName Le nom de la feuille Excel.
     */
    private static <T> void autoSizeColumns(Sheet sheet, String sheetName) {
        int numColumns;
        if (sheetName.equals("Professeurs")) {
            numColumns = 5;
        } else if (sheetName.equals("Modules")) {
            numColumns = 7;
        } else {
            numColumns = 0;
        }

        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
