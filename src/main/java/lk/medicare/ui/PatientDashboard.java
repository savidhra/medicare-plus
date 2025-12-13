package lk.medicare.ui;

import lk.medicare.model.User;
import lk.medicare.model.Appointment;
import lk.medicare.dao.AppointmentDao;
import lk.medicare.dao.NotificationDao;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class PatientDashboard extends JFrame {

    public PatientDashboard(User user) {
        super("MediCare Plus - Patient Portal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("Welcome, " + user.fullName);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(33, 150, 243));
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        if(user.patientId > 0) checkUpcomingAppointments(user.patientId);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));

        tabs.addTab("Book Appointment", new ScheduleAppointmentPanel(user));
        tabs.addTab("My History", new PatientAppointmentsPanel(user.patientId));
        tabs.addTab("My Inbox", new PatientNotificationPanel(user.patientId));

        add(tabs, BorderLayout.CENTER);
    }

    private void checkUpcomingAppointments(int patientId) {
        new Thread(() -> {
            try {
                List<Appointment> list = new AppointmentDao().getAppointmentsByPatient(patientId);
                LocalDate tomorrow = LocalDate.now().plusDays(1);

                for(Appointment a : list) {
                    if (a.getAppointmentDateTime() == null) continue;

                    Object rawDate = a.getAppointmentDateTime();
                    LocalDate apptDate = null;

                    if (rawDate != null) {
                        apptDate = ((LocalDateTime) rawDate).toLocalDate();
                    }

                    if(apptDate.equals(tomorrow) && "SCHEDULED".equalsIgnoreCase(a.getStatus())) {
                        new NotificationDao().send("PATIENT", patientId,
                                "Reminder: You have an appointment tomorrow with " + a.getDoctorName());
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}