package lk.medicare.ui;
import lk.medicare.dao.reportDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.Vector;


public class reportPanel extends JPanel{
    public reportPanel() {
        setLayout(new BorderLayout(16,16));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel title = new JLabel("Monthly Reports", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        JTable table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnLoad = new JButton("Load Statistics");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnLoad);
        add(south, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> {
            try {
                ResultSet rs = new reportDAO().getAppointmentStats();
                Vector<String> cols = new Vector<>(); cols.add("Status"); cols.add("Total");
                Vector<Vector<Object>> data = new Vector<>();
                while(rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("Status"));
                    row.add(rs.getInt("Total"));
                    data.add(row);
                }
                table.setModel(new DefaultTableModel(data, cols));
            } catch(Exception ex) { ex.printStackTrace(); }
        });
    }
}



