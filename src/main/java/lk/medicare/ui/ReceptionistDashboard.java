package lk.medicare.ui;

import lk.medicare.model.User;
import javax.swing.*;

public class ReceptionistDashboard extends JFrame {
    private final User currentUser;

    public ReceptionistDashboard(User u) {
        super("Receptionist");
        this.currentUser = u;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        add(new JLabel("Receptionist dashboard for " + u.username, SwingConstants.CENTER));
    }
}
