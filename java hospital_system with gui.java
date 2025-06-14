// Merged Java classes from DAO, db, entity, and gui packages
// Note: This is for review/educational purposes only. Not suitable for production use.
// All package declarations are removed. Only one public class is allowed in Java, so all others are default (non-public).
// Some class names may conflict; adjust as needed for your use case.

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;

// --- DAO/PatientDAOo.java ---
class PatientDAOo {
    public static boolean insertPatient(Patient patient) {
        String query = "INSERT INTO patients (first_name, last_name, gender, age, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, patient.getName());
            stmt.setString(2, ""); // last_name not present in Patient class
            stmt.setString(3, patient.getGender());
            stmt.setInt(4, patient.getAge());
            stmt.setString(5, patient.getContact());
            stmt.setString(6, ""); // address not present in Patient class
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patients";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Patient patient = new Patient(
                    rs.getString("first_name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    ""
                );
                patient.setId(rs.getInt("id"));
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
}

// --- db/DBConnection.java ---
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

// --- entity/Patient.java ---
class Patient {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String contact;
    private String selectedDoctor;
    public Patient(String name, int age, String gender, String contact, String selectedDoctor) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        setContact(contact);
        this.selectedDoctor = selectedDoctor;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getContact() { return contact; }
    public String getSelectedDoctor() { return selectedDoctor; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setContact(String contact) {
        if (!contact.matches("^09\\d{8}$")) {
            throw new IllegalArgumentException("Contact must be exactly 10 digits and start with '09'.");
        }
        this.contact = contact;
    }
    public void setSelectedDoctor(String selectedDoctor) {
        this.selectedDoctor = selectedDoctor;
    }
}

// --- entity/Doctor.java ---
class DoctorStage extends Stage {
    private ComboBox<String> doctorList;
    public DoctorStage() {
        setTitle("Available Dentists");
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        doctorList = new ComboBox<>();
        loadAvailableDentists();
        Label label = new Label("Choose Dentist:");
        Button nextBtn = new Button("Next (Book Appointment)");
        nextBtn.setOnAction(e -> {
            String selectedDoctor = doctorList.getValue();
            if (selectedDoctor != null && !selectedDoctor.isEmpty()) {
                try {
                    int doctorId = Integer.parseInt(selectedDoctor.split(":")[0].trim());
                    new AppointmentStage(doctorId);
                    this.close();
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid doctor ID format.").showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Please select a dentist.").showAndWait();
            }
        });
        root.getChildren().addAll(label, doctorList, nextBtn);
        Scene scene = new Scene(root, 400, 200);
        setScene(scene);
        show();
    }
    private void loadAvailableDentists() {
        try (Connection conn = DBConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(
                     "SELECT doctor_id, name FROM doctor WHERE specialization = 'Dentist' AND availability = TRUE")) {
            while (rs.next()) {
                int id = rs.getInt("doctor_id");
                String name = rs.getString("name");
                doctorList.getItems().add(id + ": " + name);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load dentists: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }
}

// --- entity/Appointment.java ---
class AppointmentStage extends Stage {
    private TextField patientIdField, dateField;
    private int doctorId;
    public AppointmentStage(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Book Appointment");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        Label patientIdLabel = new Label("Patient ID:");
        patientIdField = new TextField();
        grid.add(patientIdLabel, 0, 0);
        grid.add(patientIdField, 1, 0);
        Label dateLabel = new Label("Appointment Date (YYYY-MM-DD):");
        dateField = new TextField();
        grid.add(dateLabel, 0, 1);
        grid.add(dateField, 1, 1);
        Button bookBtn = new Button("Book Appointment");
        bookBtn.setOnAction(e -> bookAppointment());
        grid.add(bookBtn, 1, 2);
        Scene scene = new Scene(grid, 400, 200);
        setScene(scene);
        show();
    }
    private void bookAppointment() {
        try {
            int patientId = Integer.parseInt(patientIdField.getText().trim());
            String date = dateField.getText().trim();
            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO appointment (patient_id, doctor_id, appointment_date, status) VALUES (?, ?, ?, 'Scheduled')";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, patientId);
                stmt.setInt(2, doctorId);
                stmt.setString(3, date);
                stmt.executeUpdate();
                new Alert(Alert.AlertType.INFORMATION, "Appointment booked successfully!").showAndWait();
                this.close();
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid patient ID. Please enter a number.").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to book appointment: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }
}

// --- gui/RoleSelection.java ---
class RoleSelection extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Select Role");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        Button patientBtn = new Button("I am a Patient");
        Button doctorBtn = new Button("I am a Doctor");
        patientBtn.setOnAction(e -> {
            new PatientStage();
            primaryStage.close();
        });
        doctorBtn.setOnAction(e -> {
            new DoctorStage();
            primaryStage.close();
        });
        root.getChildren().addAll(patientBtn, doctorBtn);
        Scene scene = new Scene(root, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

// --- gui/RegisterPatient.java ---
class RegisterPatientStage extends Stage {
    public RegisterPatientStage() {
        setTitle("Register Patient");
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().add(new Label("Patient Registration not implemented yet."));
        Scene scene = new Scene(root, 300, 100);
        setScene(scene);
        show();
    }
}

// --- gui/patient.java ---
class PatientStage extends Stage {
    TextField nameField, ageField, contactField;
    ComboBox<String> genderCombo;
    ComboBox<String> doctorList;
    public PatientStage() {
        setTitle("Patient Registration");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        Label nameLabel = new Label("Name:");
        nameField = new TextField();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        Label ageLabel = new Label("Age:");
        ageField = new TextField();
        grid.add(ageLabel, 0, 1);
        grid.add(ageField, 1, 1);
        Label genderLabel = new Label("Gender:");
        genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female");
        grid.add(genderLabel, 0, 2);
        grid.add(genderCombo, 1, 2);
        Label contactLabel = new Label("Contact:");
        contactField = new TextField();
        grid.add(contactLabel, 0, 3);
        grid.add(contactField, 1, 3);
        Label doctorLabel = new Label("Choose Dentist:");
        doctorList = new ComboBox<>();
        loadAvailableDentists();
        grid.add(doctorLabel, 0, 4);
        grid.add(doctorList, 1, 4);
        Button submit = new Button("Register Patient");
        submit.setOnAction(e -> addPatient());
        grid.add(submit, 1, 5);
        Scene scene = new Scene(grid, 400, 350);
        setScene(scene);
        show();
    }
    private void loadAvailableDentists() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT doctor_id, name FROM doctor WHERE specialization = 'Dentist' AND availability = TRUE";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("doctor_id");
                String name = rs.getString("name");
                doctorList.getItems().add(id + ": " + name);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error loading doctors: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }
    private void addPatient() {
        try {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = genderCombo.getValue();
            String contact = contactField.getText();
            String selectedDoctor = doctorList.getValue();
            if (selectedDoctor == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a doctor.").showAndWait();
                return;
            }
            if (!contact.matches("^09\\d{8}$")) {
                new Alert(Alert.AlertType.WARNING, "Contact must be exactly 10 digits and start with '09'.").showAndWait();
                return;
            }
            int doctorId = Integer.parseInt(selectedDoctor.split(":")[0]);
            Patient patient = new Patient(name, age, gender, contact, selectedDoctor);
            PatientDAOo.insertPatient(patient);
            new Alert(Alert.AlertType.INFORMATION, "Patient registered successfully!").showAndWait();
            this.close();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
        }
    }
}

// --- gui/MainMenu.java ---
class MainMenuStage extends Application {
    public void start(Stage var1) {
        var1.setTitle("Hospital Management System");
        VBox var2 = new VBox(10);
        var2.setPadding(new Insets(20));
        Button var3 = new Button("Register Patient");
        var3.setOnAction((var0) -> new RegisterPatientStage());
        Button var4 = new Button("Choose Dentist");
        var4.setOnAction((var0) -> new DoctorStage());
        var2.getChildren().addAll(var3, var4);
        Scene var5 = new Scene(var2, 300, 150);
        var1.setScene(var5);
        var1.show();
    }
    public static void main(String[] var0) {
        launch(var0);
    }
}

// --- gui/main.java ---
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hospital Management System");
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        Button registerBtn = new Button("Register Patient");
        registerBtn.setOnAction(e -> new RegisterPatientStage());
        Button chooseDoctorBtn = new Button("Choose Dentist");
        chooseDoctorBtn.setOnAction(e -> new DoctorStage());
        root.getChildren().addAll(registerBtn, chooseDoctorBtn);
        Scene scene = new Scene(root, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
