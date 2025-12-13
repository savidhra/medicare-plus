package lk.medicare.ui;

import lk.medicare.dao.NotificationDao;
import javax.swing.*;
import java.awt.*;

public class PatientNotificationPanel extends JPanel {

    // Moved model to class level for easier access
    private DefaultListModel<String> model = new DefaultListModel<>();

    public PatientNotificationPanel(int patientId) {
        setLayout(new BorderLayout(16,16));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        // 1. Header
        JLabel title = new JLabel("My Inbox", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(new Color(33, 150, 243)); // Professional Blue
        add(title, BorderLayout.NORTH);

        // 2. Message List
        JList<String> list = new JList<>(model);
        list.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(list), BorderLayout.CENTER);

        // 3. Refresh Button
        JButton btnRefresh = new JButton("Check for Messages");
        add(btnRefresh, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadMessages(patientId));

        // 4. Auto-load on startup
        loadMessages(patientId);
    }

    private void loadMessages(int patientId) {
        try {
            model.clear();
            // Calls the NotificationDao method we created earlier
            for(String msg : new NotificationDao().getPatientMessages(patientId)) {
                model.addElement(msg);
            }

            if (model.isEmpty()) {
                model.addElement("No new messages.");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading messages: " + ex.getMessage());
        }
    }
}