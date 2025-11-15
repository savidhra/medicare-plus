package lk.medicare.ui;

import lk.medicare.model.User;
import javax.swing.*;

public class DoctorDashboard extends JFrame {
    private final User currentUser;

    public DoctorDashboard(User u) {
        super("Doctor");
        this.currentUser = u;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        add(new JLabel("Doctor dashboard for " + u.username, SwingConstants.CENTER));
    }
}
