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
        super("MediCare Plus - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 180);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(3,2,6,6));
        p.add(new JLabel("Username:"));
        p.add(txtUser);
        p.add(new JLabel("Password:"));
        p.add(txtPass);
        p.add(new JLabel());
        p.add(btnLogin);

        add(p);

        btnLogin.addActionListener(e -> onLogin());
    }

    private void onLogin() {
        try {
            UserDao dao = new UserDao();
            User u = dao.authenticate(txtUser.getText().trim(), new String(txtPass.getPassword()));
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Route by role
            dispose();
            switch (u.role) {
                case "PATIENT" -> new PatientDashboard(u).setVisible(true);
                case "DOCTOR" -> new DoctorDashboard(u).setVisible(true);
                case "RECEPTIONIST" -> new ReceptionistDashboard(u).setVisible(true);
                case "ADMIN" -> new AdminDashboard(u).setVisible(true);
                default -> JOptionPane.showMessageDialog(null, "Unknown role: " + u.role);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

