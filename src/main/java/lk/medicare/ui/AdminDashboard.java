package lk.medicare.ui;

import lk.medicare.model.User;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard(User user) {
        // --- 1. UI Setup (Consistent with other Dashboards) ---
        super("SwingMed - Admin Portal");
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Closes app on exit
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 2. Blue Header Bar ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243)); // Professional Blue
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("Admin Panel: " + user.fullName);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(33, 150, 243));

        // LOGOUT LOGIC
        btnLogout.addActionListener(e -> {
            dispose(); // Close this window
            new LoginFrame().setVisible(true); // Open Login again
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- 3. Main Tabs ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // TAB 1: Dashboard / Overview (Placeholder)
        JPanel pnlHome = new JPanel(new BorderLayout());
        JLabel lblMsg = new JLabel("System Overview & Statistics will appear here.", SwingConstants.CENTER);
        lblMsg.setFont(new Font("SansSerif", Font.ITALIC, 16));
        lblMsg.setForeground(Color.GRAY);
        pnlHome.add(lblMsg, BorderLayout.CENTER);
        tabs.addTab("Overview", pnlHome);

        // TAB 2: Manage Users (Placeholder for future functionality)
        // You can create a 'UserManagementPanel' class later and add it here
        JPanel pnlUsers = new JPanel();
        pnlUsers.add(new JLabel("User Management Module (Coming Soon)"));
        tabs.addTab("Manage Users", pnlUsers);

        // TAB 3: System Settings (Placeholder)
        JPanel pnlSettings = new JPanel();
        pnlSettings.add(new JLabel("System Configurations"));
        tabs.addTab("Settings", pnlSettings);

        add(tabs, BorderLayout.CENTER);
    }
}