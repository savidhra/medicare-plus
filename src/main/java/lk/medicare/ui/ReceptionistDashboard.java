package lk.medicare.ui;

import lk.medicare.model.User;
import javax.swing.*;
import java.awt.*;

public class ReceptionistDashboard extends JFrame {

    public ReceptionistDashboard(User user) {
        super("MediCare Plus - Receptionist Portal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("Welcome, " + user.fullName);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(33, 150, 243));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Tabs ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));

        tabs.addTab("Manage Patients", new PatientPanel());
        tabs.addTab("Manage Doctors", new DoctorPanel());
        tabs.addTab("Appointments Status", new StatusPanel());
        tabs.addTab("Smart Allocator", new AutoAssignPanel());

        // --- THIS WAS MISSING IN YOUR SCREENSHOT ---
        tabs.addTab("Reports & Analytics", new ReportPanel());

        add(tabs, BorderLayout.CENTER);
    }
}