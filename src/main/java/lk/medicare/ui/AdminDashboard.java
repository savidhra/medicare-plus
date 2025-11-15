package lk.medicare.ui;

import lk.medicare.model.User;
import javax.swing.*;

public class AdminDashboard extends JFrame {
    private final User currentUser;

    public AdminDashboard(User u) {
        super("Admin");
        this.currentUser = u;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        add(new JLabel("Admin dashboard for " + u.username, SwingConstants.CENTER));
    }
}
