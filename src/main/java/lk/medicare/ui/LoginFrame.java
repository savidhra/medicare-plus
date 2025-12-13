package lk.medicare.ui;

import lk.medicare.dao.UserDao;
import lk.medicare.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUser = new JTextField(15);
    private JPasswordField txtPass = new JPasswordField(15);
    private JButton btnLogin = new JButton("Login");

    public LoginFrame() {
        super("SwingMed - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 220); // Slightly larger for better spacing
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new GridLayout(3, 2, 10, 10)); // Added gap

        mainPanel.add(new JLabel("Username:"));
        mainPanel.add(txtUser);

        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(txtPass);

        mainPanel.add(new JLabel("")); // Spacer
        mainPanel.add(btnLogin);

        add(mainPanel);

        // Allow "Enter" key to trigger login
        getRootPane().setDefaultButton(btnLogin);

        btnLogin.addActionListener(e -> onLogin());
    }

    private void onLogin() {
        try {
            UserDao dao = new UserDao();
            // authenticate returns the fully populated User object (with PatientId/DoctorId)
            User u = dao.authenticate(txtUser.getText().trim(), new String(txtPass.getPassword()));

            if (u == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Route by role
            // NOTE: Dashboards now only need the 'User' object because it contains all IDs.
            dispose(); // Close login window

            switch (u.role.toUpperCase()) { // Ensure case-insensitivity
                case "PATIENT" -> new PatientDashboard(u).setVisible(true);
                case "DOCTOR" -> new DoctorDashboard(u).setVisible(true);
                case "RECEPTIONIST" -> new ReceptionistDashboard(u).setVisible(true);
                case "ADMIN" -> {
                    // Assuming AdminDashboard exists, otherwise remove this case
                    // new AdminDashboard(u).setVisible(true);
                    JOptionPane.showMessageDialog(this, "Admin Dashboard coming soon!");
                }
                default -> JOptionPane.showMessageDialog(null, "Unknown role: " + u.role);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}