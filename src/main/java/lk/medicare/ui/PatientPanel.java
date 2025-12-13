package lk.medicare.ui;

import lk.medicare.dao.PatientDao;
import lk.medicare.model.Patient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;

public class PatientPanel extends JPanel {

    // --- Tab 1: Register Components ---
    JTextField txtFName = new JTextField(15);
    JTextField txtLName = new JTextField(15);
    JTextField txtPhone = new JTextField(15);
    JTextField txtEmail = new JTextField(20);
    JTextField txtDob   = new JTextField(15);
    JTextField txtUser  = new JTextField(15);
    JPasswordField txtPass = new JPasswordField(15);

    // --- Tab 2: Manage Components ---
    JTable patTable = new JTable();
    DefaultTableModel patModel;

    // Edit Fields (For the "Save" feature)
    JTextField editFName = new JTextField(10);
    JTextField editLName = new JTextField(10);
    JTextField editPhone = new JTextField(10);
    JTextField editEmail = new JTextField(15);
    int selectedPatientId = -1; // Tracks which patient is being edited

    public PatientPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        // -----------------------------
        // TAB 1: REGISTER NEW PATIENT
        // -----------------------------
        JPanel pnlReg = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);

        addRegField(pnlReg, gc, 0, "First Name:", txtFName);
        addRegField(pnlReg, gc, 1, "Last Name:", txtLName);
        addRegField(pnlReg, gc, 2, "Phone:", txtPhone);
        addRegField(pnlReg, gc, 3, "Email:", txtEmail);
        addRegField(pnlReg, gc, 4, "DOB (yyyy-mm-dd):", txtDob);
        addRegField(pnlReg, gc, 5, "Username:", txtUser);
        addRegField(pnlReg, gc, 6, "Password:", txtPass);

        JButton btnSave = new JButton("Register Patient");
        gc.gridx=1; gc.gridy=7; pnlReg.add(btnSave, gc);
        btnSave.addActionListener(e -> register());

        tabs.addTab("Register New", pnlReg);

        // -----------------------------
        // TAB 2: VIEW & MANAGE (With Save Button)
        // -----------------------------
        JPanel pnlManage = new JPanel(new BorderLayout(10,10));
        pnlManage.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // 1. Table
        patModel = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Phone", "Email"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        patTable.setModel(patModel);
        patTable.setRowHeight(24);
        patTable.getSelectionModel().addListSelectionListener(e -> fillEditFields()); // Auto-fill on click
        pnlManage.add(new JScrollPane(patTable), BorderLayout.CENTER);

        // 2. Edit Form (South Panel)
        JPanel pnlEdit = new JPanel(new GridBagLayout());
        pnlEdit.setBorder(BorderFactory.createTitledBorder("Edit Selected Patient"));
        GridBagConstraints egc = new GridBagConstraints();
        egc.insets = new Insets(4, 4, 4, 4);

        // Row 1
        egc.gridx=0; egc.gridy=0; pnlEdit.add(new JLabel("First Name:"), egc);
        egc.gridx=1; pnlEdit.add(editFName, egc);
        egc.gridx=2; pnlEdit.add(new JLabel("Last Name:"), egc);
        egc.gridx=3; pnlEdit.add(editLName, egc);

        // Row 2
        egc.gridx=0; egc.gridy=1; pnlEdit.add(new JLabel("Phone:"), egc);
        egc.gridx=1; pnlEdit.add(editPhone, egc);
        egc.gridx=2; pnlEdit.add(new JLabel("Email:"), egc);
        egc.gridx=3; pnlEdit.add(editEmail, egc);

        // Row 3 (Buttons)
        JPanel btnPanel = new JPanel();
        JButton btnUpdate = new JButton("Save Changes");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh List");

        btnUpdate.setBackground(new Color(0, 128, 0)); // Green for Save
        btnUpdate.setForeground(Color.WHITE);
        btnDelete.setForeground(Color.RED);

        btnPanel.add(btnRefresh);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        egc.gridx=0; egc.gridy=2; egc.gridwidth=4; pnlEdit.add(btnPanel, egc);
        pnlManage.add(pnlEdit, BorderLayout.SOUTH);

        // Events
        btnRefresh.addActionListener(e -> loadTable());
        btnUpdate.addActionListener(e -> saveChanges());
        btnDelete.addActionListener(e -> deleteSelected());

        tabs.addTab("View & Manage", pnlManage);
        add(tabs, BorderLayout.CENTER);

        loadTable();
    }

    // --- LOGIC ---

    private void fillEditFields() {
        int row = patTable.getSelectedRow();
        if (row >= 0) {
            selectedPatientId = Integer.parseInt(patModel.getValueAt(row, 0).toString());
            editFName.setText(patModel.getValueAt(row, 1).toString());
            editLName.setText(patModel.getValueAt(row, 2).toString());
            editPhone.setText(patModel.getValueAt(row, 3).toString());
            editEmail.setText(patModel.getValueAt(row, 4).toString());
        }
    }

    private void saveChanges() {
        if (selectedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient to edit.");
            return;
        }
        try {
            new PatientDao().updatePatient(
                    selectedPatientId,
                    editFName.getText(),
                    editLName.getText(),
                    editPhone.getText(),
                    editEmail.getText()
            );
            JOptionPane.showMessageDialog(this, "Changes Saved Successfully!");
            loadTable();
            clearEditFields();
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteSelected() {
        if (selectedPatientId == -1) return;
        if(JOptionPane.showConfirmDialog(this, "Delete this patient?") == JOptionPane.YES_OPTION) {
            try {
                new PatientDao().deletePatient(selectedPatientId);
                loadTable();
                clearEditFields();
            } catch(Exception e) { e.printStackTrace(); }
        }
    }

    private void clearEditFields() {
        selectedPatientId = -1;
        editFName.setText(""); editLName.setText("");
        editPhone.setText(""); editEmail.setText("");
    }

    private void register() {
        try {
            Patient p = new Patient(1, txtFName.getText(), txtLName.getText(), txtPhone.getText(), txtEmail.getText(), Date.valueOf(txtDob.getText()));
            new PatientDao().registerPatient(p, txtUser.getText(), new String(txtPass.getPassword()));
            JOptionPane.showMessageDialog(this, "Registered!");
            loadTable();
            txtFName.setText(""); txtLName.setText(""); txtPhone.setText("");
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void loadTable() {
        try {
            patModel.setRowCount(0);
            for(Patient p : new PatientDao().getAll()) {
                patModel.addRow(new Object[]{p.patientId, p.firstName, p.lastName, p.phone, p.email});
            }
        } catch(Exception e) {}
    }

    private void addRegField(JPanel p, GridBagConstraints gc, int y, String lbl, Component cmp) {
        gc.gridx=0; gc.gridy=y; p.add(new JLabel(lbl), gc);
        gc.gridx=1; p.add(cmp, gc);
    }
}