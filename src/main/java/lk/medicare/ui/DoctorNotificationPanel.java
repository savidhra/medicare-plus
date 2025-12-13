package lk.medicare.ui;
import lk.medicare.dao.NotificationDao;
import javax.swing.*;
import java.awt.*;

public class DoctorNotificationPanel extends JPanel {
    public DoctorNotificationPanel(int doctorId) {
        setLayout(new BorderLayout(16,16));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel title = new JLabel("Doctor Alerts", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(Color.RED);
        add(title, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh Alerts");
        add(btnRefresh, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> {
            try {
                model.clear();
                for(String msg : new NotificationDao().getDoctorAlerts(doctorId)) {
                    model.addElement(msg);
                }
            } catch(Exception ex) { ex.printStackTrace(); }
        });
    }
}

