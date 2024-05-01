package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class StudentApplication {

    private JFrame frame;
    private JTextField txtId, txtName, txtCourse, txtFee, txtStandardName;
    private JComboBox<String> comboTeacherName;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/school_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Tan@bha24";

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    StudentApplication window = new StudentApplication();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public StudentApplication() {
        initialize();
        fetchTeacherNames();
    }

    private void initialize() {
        frame = new JFrame("Student Details");
        frame.setBounds(100, 100, 750, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(25, 25));

        JPanel panel_center = new JPanel();
        panel_center.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel_center.setLayout(new GridLayout(6, 5, 10, 20));
        frame.getContentPane().add(panel_center, BorderLayout.CENTER);

        JLabel lblStudentId = new JLabel("Student ID:");
        lblStudentId.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_center.add(lblStudentId);

        txtId = new JTextField();
        txtId.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel_center.add(txtId);

        JLabel lblName = new JLabel("Student Name:");
        lblName.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_center.add(lblName);

        txtName = new JTextField();
        txtName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel_center.add(txtName);

        JLabel lblCourse = new JLabel("Course:");
        lblCourse.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_center.add(lblCourse);

        txtCourse = new JTextField();
        txtCourse.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel_center.add(txtCourse);

        JLabel lblFee = new JLabel("Fee:");
        lblFee.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_center.add(lblFee);

        txtFee = new JTextField();
        txtFee.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel_center.add(txtFee);

        JLabel lblTeacherName = new JLabel("Teacher Name:");
        lblTeacherName.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_center.add(lblTeacherName);

        comboTeacherName = new JComboBox<>();
        comboTeacherName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel_center.add(comboTeacherName);

        JLabel lblStandardName = new JLabel("Class:");
        lblStandardName.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_center.add(lblStandardName);

        txtStandardName = new JTextField();
        txtStandardName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel_center.add(txtStandardName);

        // Add buttons with placeholder text
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton btnClear = new JButton("Clear");
        btnClear.setPreferredSize(new Dimension(100, 30));
        btnClear.setFont(new Font("Tahoma", Font.BOLD, 14));
        buttonPanel.add(btnClear);
        btnClear.addActionListener(e -> clearFields());

        JButton btnAdd = new JButton("Create");
        btnAdd.setPreferredSize(new Dimension(100, 30));
        btnAdd.setFont(new Font("Tahoma", Font.BOLD, 14));
        buttonPanel.add(btnAdd);
        btnAdd.addActionListener(e -> insertStudent());

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setPreferredSize(new Dimension(100, 30));
        btnUpdate.setFont(new Font("Tahoma", Font.BOLD, 14));
        buttonPanel.add(btnUpdate);
        btnUpdate.addActionListener(e -> updateStudent());

        JButton btnSearch = new JButton("Search");
        btnSearch.setPreferredSize(new Dimension(100, 30));
        btnSearch.setFont(new Font("Tahoma", Font.BOLD, 14));
        buttonPanel.add(btnSearch);
        btnSearch.addActionListener(e -> searchStudent());

        JButton btnDelete = new JButton("Delete");
        btnDelete.setPreferredSize(new Dimension(100, 30));
        btnDelete.setFont(new Font("Tahoma", Font.BOLD, 14));
        buttonPanel.add(btnDelete);
        btnDelete.addActionListener(e -> deleteStudent());

        JButton btnView = new JButton("Read");
        btnView.setPreferredSize(new Dimension(100, 30));
        btnView.setFont(new Font("Tahoma", Font.BOLD, 14));
        buttonPanel.add(btnView);
        btnView.addActionListener(e -> viewStudents());

        frame.setVisible(true);
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtCourse.setText("");
        txtFee.setText("");
        txtStandardName.setText("");
    }

    private void fetchTeacherNames() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String query = "SELECT name FROM teachers";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String teacherName = rs.getString("name");
                comboTeacherName.addItem(teacherName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String generateStudentId() {
        int lastId = getLastStudentIdFromDatabase();
        int newId = lastId + 1;
        return String.format("%02d", newId);
    }

    private int getLastStudentIdFromDatabase() {
        int lastId = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String query = "SELECT MAX(id) FROM students";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                lastId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lastId;
    }

    
    private void insertStudent() {
        String name = txtName.getText();
        String course = txtCourse.getText();
        String fee = txtFee.getText();
        String teacherName = (String) comboTeacherName.getSelectedItem();
        String standardName = txtStandardName.getText();
        String studentId;

        // Check if student ID is manually entered
        if (!txtId.getText().isEmpty()) {
            // Use manually entered ID
            studentId = txtId.getText();
        } else {
            // Generate the next sequential ID
            studentId = generateStudentId();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String query = "INSERT INTO students (id, name, course, fee, teacher_name, standard_name) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, studentId); 
            statement.setString(2, name);
            statement.setString(3, course);
            statement.setString(4, fee);
            statement.setString(5, teacherName);
            statement.setString(6, standardName);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Student created successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    

    private void updateStudent() {
        int id = Integer.parseInt(txtId.getText());
        String name = txtName.getText();
        String course = txtCourse.getText();
        String fee = txtFee.getText();
        String teacherName = (String) comboTeacherName.getSelectedItem();
        String standardName = txtStandardName.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String query = "UPDATE students SET name=?, course=?, fee=?, teacher_name=?, standard_name=? WHERE id=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, course);
            statement.setString(3, fee);
            statement.setString(4, teacherName);
            statement.setString(5, standardName);
            statement.setInt(6, id);
            int updatedRows = statement.executeUpdate();
            if (updatedRows > 0)
                JOptionPane.showMessageDialog(frame, "Data updated successfully!");
            else
                JOptionPane.showMessageDialog(frame, "No data found for ID: " + id);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchStudent() {
        int id = Integer.parseInt(txtId.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM students WHERE id=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtCourse.setText(rs.getString("course"));
                txtFee.setText(rs.getString("fee"));
                comboTeacherName.setToolTipText(rs.getString("teacher_name"));
                txtStandardName.setText(rs.getString("standard_name"));
            } else {
                JOptionPane.showMessageDialog(frame, "No data found for ID: " + id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteStudent() {
        int id = Integer.parseInt(txtId.getText());

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String query = "DELETE FROM students WHERE id=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, id);
            int deletedRows = statement.executeUpdate();
            if (deletedRows > 0) {
                clearFields();
                JOptionPane.showMessageDialog(frame, "Data deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "No data found for ID: " + id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void viewStudents() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM students";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            StringBuilder data = new StringBuilder();
            while (rs.next()) {
                data.append("ID: ").append(rs.getInt("id")).append(", ")
                    .append("Student Name: ").append(rs.getString("name")).append(", ")
                    .append("Course: ").append(rs.getString("course")).append(", ")
                    .append("Fee: ").append(rs.getString("fee")).append(", ")
                    .append("Teacher Name: ").append(rs.getString("teacher_name")).append(", ")
                    .append("Class: ").append(rs.getString("standard_name")).append("\n");
            }
            JOptionPane.showMessageDialog(frame, data.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
