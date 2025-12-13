package lk.medicare.ui;

import lk.medicare.dao.DoctorDao;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DoctorScheduleViewPanel extends JPanel {

    private final int doctorId;
    private final DefaultListModel<String> model = new DefaultListModel<>();

    public DoctorScheduleViewPanel(int doctorId) {
        this.doctorId = doctorId;

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("My Weekly Schedule", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        JList<String> list = new JList<>(model);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh Schedule");
        btnRefresh.addActionListener(e -> loadSchedule());
        add(btnRefresh, BorderLayout.SOUTH);

        loadSchedule();
    }

    private void loadSchedule() {
        try {
            model.clear();
            List<String> rows = new DoctorDao().getScheduleForDoctor(doctorId);
            for (String r : rows) model.addElement(r);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading schedule: " + ex.getMessage());
        }
    }
}
