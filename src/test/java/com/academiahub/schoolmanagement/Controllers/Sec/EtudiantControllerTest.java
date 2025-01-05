package com.academiahub.schoolmanagement.Controllers.Sec;

import com.academiahub.schoolmanagement.DAO.EtudiantDAO;
import com.academiahub.schoolmanagement.Models.Etudiant;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EtudiantControllerTest {
    private static Connection connection;
    private EtudiantController controller;
    private EtudiantDAO etudiantDAO;
    private static CountDownLatch latch;

    @BeforeAll
    static void setUpClass() throws InterruptedException {
        latch = new CountDownLatch(1);
        Platform.startup(() -> {
            latch.countDown();
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new ExceptionInInitializerError("JavaFX platform did not initialize correctly.");
        }

        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
            Statement statement = connection.createStatement();

            statement.execute("""
                CREATE TABLE etudiants (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    matricule VARCHAR(50) NOT NULL,
                    nom VARCHAR(100) NOT NULL,
                    prenom VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL,
                    promotion VARCHAR(50) NOT NULL
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeTableView(TableView<Etudiant> tableView) {
        // Initialize columns
        TableColumn<Etudiant, String> matriculeCol = new TableColumn<>("Matricule");
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));

        TableColumn<Etudiant, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Etudiant, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableColumn<Etudiant, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Etudiant, String> promotionCol = new TableColumn<>("Promotion");
        promotionCol.setCellValueFactory(new PropertyValueFactory<>("promotion"));

        tableView.getColumns().addAll(matriculeCol, nomCol, prenomCol, emailCol, promotionCol);
        tableView.setItems(FXCollections.observableArrayList());
    }

    @BeforeEach
    void setUp() throws SQLException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        CountDownLatch setupLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Clear previous test data
                Statement stmt = connection.createStatement();
                stmt.execute("DELETE FROM etudiants");

                stmt.execute("""
                    INSERT INTO etudiants (matricule, nom, prenom, email, promotion) VALUES
                    ('MAT001', 'Dupont', 'Jean', 'jean.dupont@test.com', '2024'),
                    ('MAT002', 'Martin', 'Marie', 'marie.martin@test.com', '2024')
                """);

                // Initialize controller and TableView
                controller = new EtudiantController();
                TableView<Etudiant> tableView = new TableView<>();
                initializeTableView(tableView);

                // Set TableView in controller using reflection
                Field tableField = EtudiantController.class.getDeclaredField("etudiantTable");
                tableField.setAccessible(true);
                tableField.set(controller, tableView);

                // Initialize DAO and load data
                etudiantDAO = new EtudiantDAO(connection);
                controller.setDAO();

                setupLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                setupLatch.countDown();
            }
        });

        setupLatch.await(5, TimeUnit.SECONDS);
        // Wait for any pending Platform.runLater calls to complete
        waitForFxEvents();
    }

    private void waitForFxEvents() {
        CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(doneLatch::countDown);
        try {
            doneLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testLoadEtudiants() throws Exception {
        CountDownLatch testLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                TableView<Etudiant> tableView = getTableView();
                // Force a refresh of the data
                controller.handleRefresh();
                waitForFxEvents();

                ObservableList<Etudiant> items = tableView.getItems();
                Assertions.assertEquals(2, items.size(), "Expected 2 students in the table");

                Etudiant firstStudent = items.get(0);
                Assertions.assertEquals("MAT001", firstStudent.getMatricule());
                Assertions.assertEquals("Dupont", firstStudent.getNom());
                Assertions.assertEquals("Jean", firstStudent.getPrenom());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                testLatch.countDown();
            }
        });

        testLatch.await(5, TimeUnit.SECONDS);
    }

    private TableView<Etudiant> getTableView() throws Exception {
        Field tableField = EtudiantController.class.getDeclaredField("etudiantTable");
        tableField.setAccessible(true);
        return (TableView<Etudiant>) tableField.get(controller);
    }

    @AfterEach
    void tearDown() throws SQLException {
        Platform.runLater(() -> {
            try {
                Statement stmt = connection.createStatement();
                stmt.execute("DELETE FROM etudiants");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        waitForFxEvents();
    }

    @AfterAll
    void tearDownClass() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}