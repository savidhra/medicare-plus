package lk.medicare.ui;

import lk.medicare.model.User;
import javax.swing.*;
import java.awt.*;

public class PatientDashboard extends JFrame {
    private final User currentUser;

    public PatientDashboard(User u) {
        super("Patient - Schedule Appointment");
        this.currentUser = u;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(640, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new ScheduleAppointmentPanel(u), BorderLayout.CENTER);
    }
}
