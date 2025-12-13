package lk.medicare.ui;

import lk.medicare.model.User;
import javax.swing.*;
import java.awt.*;

public class DoctorDashboard extends JFrame {

    public DoctorDashboard(User user) {
        // 1. Setup Window
        super("SwingMed - Doctor Portal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Blue Header Bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243)); // Professional Blue
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("Doctor Panel: " + user.fullName);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(33, 150, 243));

        // Logout Action
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 3. Main Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Validation: Ensure this user is actually a doctor
        int docId = user.doctorId;
        if (docId == 0) {
            JOptionPane.showMessageDialog(this, "Error: User is not linked to a Doctor Profile.");
        }

        // TAB 1: My Appointments (The panel we just fixed)
        tabs.addTab("My Appointments", new DoctorAppointmentsPanel(docId));

        // TAB 2: My Schedule (To view their working hours)
        tabs.addTab("My Schedule", new DoctorScheduleViewPanel(docId));

        // TAB 3: Notifications (Alerts)
        tabs.addTab("Alerts", new DoctorNotificationPanel(docId));

        add(tabs, BorderLayout.CENTER);
    }
}