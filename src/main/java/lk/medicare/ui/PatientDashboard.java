package lk.medicare.ui;

import lk.medicare.dao.PatientDao;
import lk.medicare.model.Patient;
import lk.medicare.model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class PatientDashboard extends JFrame {

    private final PatientDao dao = new PatientDao();

    // --- Form components ---
    JTextField txtId = new JTextField(10);
    JTextField txtFirst = new JTextField(15);
    JTextField txtLast = new JTextField(15);
    JComboBox<String> cboGender = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
    JTextField txtDob = new JTextField(12);
    JTextField txtPhone = new JTextField(15);
    JTextField txtEmail = new JTextField(20);
    JTextField txtBlood = new JTextField(5);

    JTextArea txtAllergy = new JTextArea(2, 20);
    JTextArea txtHistory = new JTextArea(2, 20);
    JTextArea txtAddress = new JTextArea(2, 20);

    JButton btnAdd = new JButton("Add Patient");
    JButton btnUpdate = new JButton("Update Patient");
    JButton btnDelete = new JButton("Delete Patient");

    JTable tbl;
    DefaultTableModel model;

    public PatientDashboard(User u) {
        super("MediCare Plus - Patient Management");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        loadTable();
        setVisible(true);
    }

    // Top colored header
    private JComponent buildHeader() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(0x0A71C6));

        JLabel lbl = new JLabel("MEDICARE PLUS - Patient Management System", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 22f));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        top.add(lbl, BorderLayout.CENTER);
        return top;
    }

    // Main container: form + buttons + table
    private JComponent buildMainPanel() {

        JPanel center = new JPanel();
        center.setLayout(new BorderLayout(10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Wrap FORM + BUTTONS in a vertical box ----
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        topSection.add(buildFormPanel());     // form
        topSection.add(Box.createVerticalStrut(10));
        topSection.add(buildButtons());       // buttons

        // Put top section inside scroll (so it never pushes buttons out)
        JScrollPane scrollTop = new JScrollPane(topSection);
        scrollTop.setBorder(null);
        scrollTop.setPreferredSize(new Dimension(1000, 350));

        // ---- Table section (fixed smaller height) ----
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(buildTablePanel(), BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(1000, 250));

        // Add to main center panel
        center.add(scrollTop, BorderLayout.NORTH);
        center.add(tablePanel, BorderLayout.SOUTH);

        return center;
    }

    private JComponent buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Manage Patients"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;

        // Row 1
        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Patient ID:"), gc);
        gc.gridx = 1; form.add(txtId, gc);

        gc.gridx = 2; form.add(new JLabel("Patient First Name:"), gc);
        gc.gridx = 3; form.add(txtFirst, gc);

        y++;
        gc.gridx = 2; gc.gridy = y; form.add(new JLabel("Patient Last Name:"), gc);
        gc.gridx = 3; form.add(txtLast, gc);

        // Row 2
        y++;
        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Gender:"), gc);
        gc.gridx = 1; form.add(cboGender, gc);

        gc.gridx = 2; form.add(new JLabel("Phone:"), gc);
        gc.gridx = 3; form.add(txtPhone, gc);

        // Row 3
        y++;
        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Date of Birth (yyyy-mm-dd):"), gc);
        gc.gridx = 1; form.add(txtDob, gc);

        gc.gridx = 2; form.add(new JLabel("Email:"), gc);
        gc.gridx = 3; form.add(txtEmail, gc);

        // Row 4
        y++;
        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Blood Group:"), gc);
        gc.gridx = 1; form.add(txtBlood, gc);

        gc.gridx = 2; form.add(new JLabel("Allergies:"), gc);
        gc.gridx = 3; form.add(new JScrollPane(txtAllergy), gc);

        // Row 5
        y++;
        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Medical History:"), gc);
        gc.gridx = 1; gc.gridwidth = 3;
        form.add(new JScrollPane(txtHistory), gc);
        gc.gridwidth = 1;

        // Row 6
        y++;
        gc.gridx = 0; gc.gridy = y; form.add(new JLabel("Address:"), gc);
        gc.gridx = 1; gc.gridwidth = 3;
        form.add(new JScrollPane(txtAddress), gc);
        gc.gridwidth = 1;

        return form;
    }

    private JComponent buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));

        styleButton(btnAdd, new Color(0x1E90FF));
        styleButton(btnUpdate, new Color(0x28A745));
        styleButton(btnDelete, new Color(0xDC3545));

        p.add(btnAdd);
        p.add(btnUpdate);
        p.add(btnDelete);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());

        return p;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
        btn.setFocusPainted(false);
    }

    private JComponent buildTablePanel() {
        model = new DefaultTableModel(
                new Object[]{"ID","First Name","Last Name","Gender","DOB","Phone","Email","Blood","Allergies","Address","History"}, 0
        ){
            public boolean isCellEditable(int r, int c){ return false; }
        };

        tbl = new JTable(model);
        tbl.setRowHeight(22);
        tbl.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setPreferredSize(new Dimension(1000, 300));

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new TitledBorder("Recorded Patients List"));
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    // Load data into table
    private void loadTable() {
        try {
            List<Patient> list = dao.getAll();
            model.setRowCount(0);
            for (Patient p : list) {
                model.addRow(new Object[]{
                        p.id, p.firstName, p.lastName, p.gender,
                        p.dob, p.phone, p.email,
                        p.bloodGroup, p.allergies, p.address, p.medicalHistory
                });
            }
        } catch (Exception e) { show(e); }
    }

    // Fill form when row clicked
    private void fillFormFromSelection() {
        int row = tbl.getSelectedRow();
        if (row == -1) return;

        txtId.setText(model.getValueAt(row, 0).toString());
        txtFirst.setText((String) model.getValueAt(row, 1));
        txtLast.setText((String) model.getValueAt(row, 2));
        cboGender.setSelectedItem(model.getValueAt(row, 3));
        txtDob.setText(String.valueOf(model.getValueAt(row, 4)));
        txtPhone.setText((String) model.getValueAt(row, 5));
        txtEmail.setText((String) model.getValueAt(row, 6));
        txtBlood.setText((String) model.getValueAt(row, 7));
        txtAllergy.setText((String) model.getValueAt(row, 8));
        txtAddress.setText((String) model.getValueAt(row, 9));
        txtHistory.setText((String) model.getValueAt(row,10));
    }

    // ---- CRUD ----
    private void onAdd() {
        try {
            dao.add(readForm(false));
            JOptionPane.showMessageDialog(this, "Patient added!");
            clearForm();
            loadTable();
        } catch (Exception e) { show(e); }
    }

    private void onUpdate() {
        try {
            dao.update(readForm(true));
            JOptionPane.showMessageDialog(this, "Patient updated!");
            clearForm();
            loadTable();
        } catch (Exception e) { show(e); }
    }

    private void onDelete() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a patient to delete.");
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete patient ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.delete(id);
                JOptionPane.showMessageDialog(this, "Patient deleted.");
                clearForm();
                loadTable();
            } catch (Exception e) { show(e); }
        }
    }

    private Patient readForm(boolean includeId) throws Exception {
        Patient p = new Patient();
        if (includeId) p.id = Integer.parseInt(txtId.getText());

        p.branchId = 1;
        p.firstName = txtFirst.getText();
        p.lastName = txtLast.getText();
        p.gender = (String) cboGender.getSelectedItem();
        p.dob = Date.valueOf(txtDob.getText());
        p.phone = txtPhone.getText();
        p.email = txtEmail.getText();
        p.bloodGroup = txtBlood.getText();
        p.address = txtAddress.getText();
        p.allergies = txtAllergy.getText();
        p.medicalHistory = txtHistory.getText();

        return p;
    }

    private void clearForm() {
        txtId.setText("");
        txtFirst.setText("");
        txtLast.setText("");
        cboGender.setSelectedIndex(0);
        txtDob.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtBlood.setText("");
        txtAllergy.setText("");
        txtHistory.setText("");
        txtAddress.setText("");
    }

    private void show(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
