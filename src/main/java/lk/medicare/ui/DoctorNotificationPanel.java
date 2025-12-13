package lk.medicare.ui;

import lk.medicare.dao.NotificationDao;
import javax.swing.*;
import java.awt.*;

public class DoctorNotificationPanel extends JPanel {

    // Moved model to class level so we can access it easily
    private DefaultListModel<String> model = new DefaultListModel<>();

    public DoctorNotificationPanel(int doctorId) {
        setLayout(new BorderLayout(16,16));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        // Header
        JLabel title = new JLabel("Doctor Alerts", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(new Color(220, 53, 69)); // Professional Red
        add(title, BorderLayout.NORTH);

        // List
        JList<String> list = new JList<>(model);
        list.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(list), BorderLayout.CENTER);

        // Refresh Button
        JButton btnRefresh = new JButton("Refresh Alerts");
        add(btnRefresh, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadAlerts(doctorId));

        // Auto-load on startup
        loadAlerts(doctorId);
    }

    private void loadAlerts(int doctorId) {
        try {
            model.clear();
            for(String msg : new NotificationDao().getDoctorAlerts(doctorId)) {
                model.addElement(msg);
            }

            if (model.isEmpty()) {
                model.addElement("No new notifications.");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading alerts: " + ex.getMessage());
        }
    }
}