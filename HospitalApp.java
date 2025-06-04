// HOSPITAL MANAGEMENT SYSTEM - ALL-IN-ONE JAVA FILE
// This file combines the main menu, patient registration, doctor selection, and appointment booking in a single Java application.
// Make sure you have MySQL running and the JDBC driver in your classpath.

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HospitalApp extends JFrame {
    public static void main(String[] args) {
        // Setup database (run only once, or check if tables exist)
        setupDatabase();
        // Launch main menu
        SwingUtilities.invokeLater(HospitalApp::new);
    }

    public HospitalApp() {
        setTitle("Hospital Management System");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        JButton registerBtn = new JButton("Register Patient");
        registerBtn.addActionListener(e -> new PatientRegistration());
        JButton registerDoctorBtn = new JButton("Register Doctor");
        registerDoctorBtn.addActionListener(e -> new DoctorRegistration());
        JButton chooseDoctorBtn = new JButton("Choose Dentist");
        chooseDoctorBtn.addActionListener(e -> new DoctorSelection());
        JButton viewPatientsBtn = new JButton("View Registered Patients");
        viewPatientsBtn.addActionListener(e -> new ViewPatients());
        JButton viewDoctorsBtn = new JButton("View Registered Doctors");
        viewDoctorsBtn.addActionListener(e -> new ViewDoctors());
        JButton viewAppointmentsBtn = new JButton("View Appointments");
        viewAppointmentsBtn.addActionListener(e -> new ViewAppointments());

        add(registerBtn);
        add(registerDoctorBtn);
        add(chooseDoctorBtn);
        add(viewPatientsBtn);
        add(viewDoctorsBtn);
        add(viewAppointmentsBtn);
        setVisible(true);
    }

    // --- Database Setup ---
    private static void setupDatabase() {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "hospital_db";
        String user = "root";
        String password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.executeUpdate("USE " + dbName);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS doctor (
                    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100),
                    specialization VARCHAR(50),
                    availability BOOLEAN
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS patient (
                    patient_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100),
                    age INT,
                    gender VARCHAR(10),
                    contact VARCHAR(10) NOT NULL,
                    selectedDoctor VARCHAR(50),
                    doctor_id INT,
                    CONSTRAINT chk_contact_valid CHECK (contact REGEXP '^09[0-9]{8}$'),
                    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id)
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS appointment (
                    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
                    patient_id INT,
                    doctor_id INT,
                    appointment_date DATE,
                    status VARCHAR(20),
                    FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
                    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id)
                )
            """);
            // Insert sample doctors if not already present
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM doctor");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.executeUpdate("""
                    INSERT INTO doctor (name, specialization, availability) VALUES
                    ('Dr. Rediet', 'Dentist', TRUE),
                    ('Dr. Natnael', 'Dentist', TRUE),
                    ('Dr. Hafize', 'Dentist', TRUE),
                    ('Dr. Yiferu', 'Dentist', TRUE),
                    ('Dr. Dawit', 'Dentist', TRUE),
                    ('Dr. Elbetel', 'Dentist', TRUE),
                    ('Dr. Genet', 'Dentist', TRUE),
                    ('Dr. Abera', 'Dentist', TRUE),
                    ('Dr. Tsdeniya', 'Dentist', TRUE),
                    ('Dr. Lidiya', 'Dentist', TRUE)
                """);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Patient Registration ---
    static class PatientRegistration extends JFrame {
        JTextField nameField, ageField, contactField;
        JComboBox<String> genderCombo, doctorList;
        public PatientRegistration() {
            setTitle("Patient Registration");
            setSize(400, 300);
            setLayout(new GridLayout(6, 2));
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            add(new JLabel("Name:"));
            nameField = new JTextField();
            add(nameField);
            add(new JLabel("Age:"));
            ageField = new JTextField();
            add(ageField);
            add(new JLabel("Gender:"));
            genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
            add(genderCombo);
            add(new JLabel("Contact (09xxxxxxxx):"));
            contactField = new JTextField();
            add(contactField);
            add(new JLabel("Choose Dentist:"));
            doctorList = new JComboBox<>();
            loadAvailableDentists();
            add(doctorList);
            JButton submit = new JButton("Register Patient");
            submit.addActionListener(e -> addPatient());
            add(submit);
            setVisible(true);
        }
        private void loadAvailableDentists() {
            try (Connection conn = getConnection()) {
                String query = "SELECT doctor_id, name FROM doctor WHERE specialization = 'Dentist' AND availability = TRUE";
                ResultSet rs = conn.createStatement().executeQuery(query);
                while (rs.next()) {
                    int id = rs.getInt("doctor_id");
                    String name = rs.getString("name");
                    doctorList.addItem(id + ": " + name);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load dentists: " + e.getMessage());
            }
        }
        private void addPatient() {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = (String) genderCombo.getSelectedItem();
            String contact = contactField.getText();
            String selectedDoctor = (String) doctorList.getSelectedItem();
            int doctorId = Integer.parseInt(selectedDoctor.split(":")[0]);
            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO patient (name, age, gender, contact, selectedDoctor, doctor_id) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setInt(2, age);
                stmt.setString(3, gender);
                stmt.setString(4, contact);
                stmt.setString(5, selectedDoctor);
                stmt.setInt(6, doctorId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Patient registered successfully!");
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to register patient: " + e.getMessage());
            }
        }
    }

    // --- Doctor Registration ---
    static class DoctorRegistration extends JFrame {
        JTextField nameField, specializationField;
        JCheckBox availableBox;
        public DoctorRegistration() {
            setTitle("Register Doctor");
            setSize(350, 200);
            setLayout(new GridLayout(4, 2));
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            add(new JLabel("Name:"));
            nameField = new JTextField();
            add(nameField);
            add(new JLabel("Specialization:"));
            specializationField = new JTextField();
            add(specializationField);
            add(new JLabel("Available:"));
            availableBox = new JCheckBox();
            availableBox.setSelected(true);
            add(availableBox);
            JButton submit = new JButton("Register Doctor");
            submit.addActionListener(e -> addDoctor());
            add(submit);
            setVisible(true);
        }
        private void addDoctor() {
            String name = nameField.getText();
            String specialization = specializationField.getText();
            boolean available = availableBox.isSelected();
            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO doctor (name, specialization, availability) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, specialization);
                stmt.setBoolean(3, available);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Doctor registered successfully!");
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to register doctor: " + e.getMessage());
            }
        }
    }

    // --- Doctor Selection and Appointment Booking ---
    static class DoctorSelection extends JFrame {
        JComboBox<String> doctorList;
        public DoctorSelection() {
            setTitle("Available Dentists");
            setSize(400, 200);
            setLayout(new FlowLayout());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            doctorList = new JComboBox<>();
            loadAvailableDentists();
            add(new JLabel("Choose Dentist:"));
            add(doctorList);
            JButton nextBtn = new JButton("Next (Book Appointment)");
            nextBtn.addActionListener(e -> {
                String selectedDoctor = (String) doctorList.getSelectedItem();
                if (selectedDoctor != null) {
                    int doctorId = Integer.parseInt(selectedDoctor.split(":")[0]);
                    new AppointmentBooking(doctorId);
                    dispose();
                }
            });
            add(nextBtn);
            setVisible(true);
        }
        private void loadAvailableDentists() {
            try (Connection conn = getConnection()) {
                String query = "SELECT doctor_id, name FROM doctor WHERE specialization = 'Dentist' AND availability = TRUE";
                ResultSet rs = conn.createStatement().executeQuery(query);
                while (rs.next()) {
                    int id = rs.getInt("doctor_id");
                    String name = rs.getString("name");
                    doctorList.addItem(id + ": " + name);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load dentists: " + e.getMessage());
            }
        }
    }

    // --- Appointment Booking ---
    static class AppointmentBooking extends JFrame {
        JTextField patientIdField, dateField;
        int doctorId;
        public AppointmentBooking(int doctorId) {
            this.doctorId = doctorId;
            setTitle("Book Appointment");
            setSize(400, 200);
            setLayout(new GridLayout(3, 2));
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            add(new JLabel("Patient ID:"));
            patientIdField = new JTextField();
            add(patientIdField);
            add(new JLabel("Appointment Date (YYYY-MM-DD):"));
            dateField = new JTextField();
            add(dateField);
            JButton bookBtn = new JButton("Book Appointment");
            bookBtn.addActionListener(e -> bookAppointment());
            add(bookBtn);
            setVisible(true);
        }
        private void bookAppointment() {
            try {
                int patientId = Integer.parseInt(patientIdField.getText());
                String date = dateField.getText();
                java.time.LocalDate enteredDate = java.time.LocalDate.parse(date);
                java.time.LocalDate today = java.time.LocalDate.now();
                if (enteredDate.getYear() < today.getYear()) {
                    JOptionPane.showMessageDialog(this, "Appointment year cannot be in the past.");
                    return;
                } else if (enteredDate.getYear() == today.getYear() && !enteredDate.isAfter(today)) {
                    JOptionPane.showMessageDialog(this, "Appointment date must be after today in the current year.");
                    return;
                }
                try (Connection conn = getConnection()) {
                    String query = "INSERT INTO appointment (patient_id, doctor_id, appointment_date, status) VALUES (?, ?, ?, 'Scheduled')";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, patientId);
                    stmt.setInt(2, doctorId);
                    stmt.setString(3, date);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
                    dispose();
                }
            } catch (java.time.format.DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to book appointment: " + e.getMessage());
            }
        }
    }

    // --- View Registered Patients ---
    static class ViewPatients extends JFrame {
        public ViewPatients() {
            setTitle("Registered Patients");
            setSize(600, 300);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            String[] columns = {"ID", "Name", "Age", "Gender", "Contact", "Selected Doctor"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            try (Connection conn = getConnection()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT patient_id, name, age, gender, contact, selectedDoctor FROM patient");
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("contact"),
                        rs.getString("selectedDoctor")
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load patients: " + e.getMessage());
            }
            add(new JScrollPane(table));
            setVisible(true);
        }
    }

    // --- View Registered Doctors ---
    static class ViewDoctors extends JFrame {
        public ViewDoctors() {
            setTitle("Registered Doctors");
            setSize(500, 300);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            String[] columns = {"ID", "Name", "Specialization", "Available"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            try (Connection conn = getConnection()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT doctor_id, name, specialization, availability FROM doctor");
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getBoolean("availability") ? "Yes" : "No"
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load doctors: " + e.getMessage());
            }
            add(new JScrollPane(table));
            setVisible(true);
        }
    }

    // --- View Appointments ---
    static class ViewAppointments extends JFrame {
        public ViewAppointments() {
            setTitle("Appointments");
            setSize(700, 300);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            String[] columns = {"ID", "Patient ID", "Doctor ID", "Date", "Status"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            try (Connection conn = getConnection()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT appointment_id, patient_id, doctor_id, appointment_date, status FROM appointment");
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("appointment_date"),
                        rs.getString("status")
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load appointments: " + e.getMessage());
            }
            add(new JScrollPane(table));
            setVisible(true);
        }
    }

    // --- Utility: Get DB Connection ---
    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital_db";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
}
