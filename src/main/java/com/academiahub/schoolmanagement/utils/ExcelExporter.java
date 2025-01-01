package com.academiahub.schoolmanagement.utils;

// ExcelExporter.java


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

public class ExcelExporter {
    private static final Logger LOGGER = Logger.getLogger(ExcelExporter.class.getName());

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
            exportToExcel(file, data);
        }
    }

    private static void exportToExcel(File file, ObservableList<Professeur> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Professeurs");
            createHeader(workbook, sheet);
            fillData(sheet, data);
            autoSizeColumns(sheet);

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                AlertUtils.showInformation("Export réussi", "Export Excel réussi !\nFichier : " + file.getName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'export Excel", e);
            AlertUtils.showError("Erreur d'export", "Impossible d'exporter les données vers Excel.");
        }
    }

    private static void createHeader(Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Nom", "Prénom", "Spécialité", "User ID"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private static void fillData(Sheet sheet, ObservableList<Professeur> data) {
        int rowNum = 1;
        for (Professeur prof : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(prof.getId());
            row.createCell(1).setCellValue(prof.getNom());
            row.createCell(2).setCellValue(prof.getPrenom());
            row.createCell(3).setCellValue(prof.getSpecialite());
            row.createCell(4).setCellValue(prof.getUserId());
        }
    }

    private static void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

// AlertUtils.java
