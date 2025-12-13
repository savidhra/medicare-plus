package lk.medicare.ui;

import lk.medicare.dao.DoctorDao;
import lk.medicare.model.Doctor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JPanel {

    // --- Tab 1: Register ---
    JTextField txtFName = new JTextField(15);
    JTextField txtLName = new JTextField(15);
    JTextField txtSpec  = new JTextField(15);
    JTextField txtUser  = new JTextField(15);
    JPasswordField txtPass = new JPasswordField(15);

    // --- Tab 2: Manage (With Save) ---
    JTable docTable = new JTable();
    DefaultTableModel docModel;

    JTextField editFName = new JTextField(10);
    JTextField editLName = new JTextField(10);
    JTextField editSpec  = new JTextField(15);
    int selectedDoctorId = -1;

    // --- Tab 3: Schedule ---
    JComboBox<String> cmbDocs = new JComboBox<>();
    JComboBox<String> cmbDay = new JComboBox<>(new String[]{"MON","TUE","WED","THU","FRI","SAT","SUN"});
    JTextField txtStart = new JTextField("09:00:00", 10);
    JTextField txtEnd = new JTextField("12:00:00", 10);

    public DoctorPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        // TAB 1: REGISTER
        JPanel pnlReg = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        addRegField(pnlReg, gc, 0, "First Name:", txtFName);
        addRegField(pnlReg, gc, 1, "Last Name:", txtLName);
        addRegField(pnlReg, gc, 2, "Specialization:", txtSpec);
        addRegField(pnlReg, gc, 3, "Username:", txtUser);
        addRegField(pnlReg, gc, 4, "Password:", txtPass);

        JButton btnSave = new JButton("Register Doctor");
        gc.gridx=1; gc.gridy=5; pnlReg.add(btnSave, gc);
        btnSave.addActionListener(e -> registerDoctor());
        tabs.addTab("Register New", pnlReg);

        // TAB 2: VIEW & MANAGE (With Edit Form)
        JPanel pnlManage = new JPanel(new BorderLayout(10,10));
        pnlManage.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        docModel = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Specialization"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        docTable.setModel(docModel);
        docTable.setRowHeight(24);
        docTable.getSelectionModel().addListSelectionListener(e -> fillEditFields()); // Auto-fill
        pnlManage.add(new JScrollPane(docTable), BorderLayout.CENTER);

        // Edit Form (South)
        JPanel pnlEdit = new JPanel(new GridBagLayout());
        pnlEdit.setBorder(BorderFactory.createTitledBorder("Edit Selected Doctor"));
        GridBagConstraints egc = new GridBagConstraints();
        egc.insets = new Insets(4,4,4,4);

        egc.gridx=0; egc.gridy=0; pnlEdit.add(new JLabel("First Name:"), egc);
        egc.gridx=1; pnlEdit.add(editFName, egc);
        egc.gridx=2; pnlEdit.add(new JLabel("Last Name:"), egc);
        egc.gridx=3; pnlEdit.add(editLName, egc);
        egc.gridx=4; pnlEdit.add(new JLabel("Spec:"), egc);
        egc.gridx=5; pnlEdit.add(editSpec, egc);

        JPanel listBtns = new JPanel();
        JButton btnUpdate = new JButton("Save Changes");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnUpdate.setBackground(new Color(0, 128, 0));
        btnUpdate.setForeground(Color.WHITE);
        btnDelete.setForeground(Color.RED);

        listBtns.add(btnRefresh); listBtns.add(btnUpdate); listBtns.add(btnDelete);

        egc.gridx=0; egc.gridy=1; egc.gridwidth=6; pnlEdit.add(listBtns, egc);
        pnlManage.add(pnlEdit, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadTableData());
        btnUpdate.addActionListener(e -> saveChanges());
        btnDelete.addActionListener(e -> deleteSelected());

        tabs.addTab("View & Manage", pnlManage);

        // TAB 3: SCHEDULE (Simplified)
        JPanel pnlSch = new JPanel(new GridBagLayout());
        gc.gridx=0; gc.gridy=0; pnlSch.add(new JLabel("Select Doctor:"), gc);
        gc.gridx=1; pnlSch.add(cmbDocs, gc);

        JButton btnRefDrop = new JButton("↻");
        btnRefDrop.addActionListener(e -> loadDropdown());
        gc.gridx=2; pnlSch.add(btnRefDrop, gc);

        gc.gridx=0; gc.gridy=1; pnlSch.add(new JLabel("Day:"), gc);
        gc.gridx=1; pnlSch.add(cmbDay, gc);
        gc.gridx=0; gc.gridy=2; pnlSch.add(new JLabel("Start:"), gc);
        gc.gridx=1; pnlSch.add(txtStart, gc);
        gc.gridx=0; gc.gridy=3; pnlSch.add(new JLabel("End:"), gc);
        gc.gridx=1; pnlSch.add(txtEnd, gc);

        JButton btnAddSch = new JButton("Add Schedule");
        gc.gridx=1; gc.gridy=4; pnlSch.add(btnAddSch, gc);
        btnAddSch.addActionListener(e -> addSchedule());

        tabs.addTab("Manage Schedule", pnlSch);
        add(tabs, BorderLayout.CENTER);

        loadTableData();
        loadDropdown();
    }

    // --- LOGIC ---

    private void fillEditFields() {
        int row = docTable.getSelectedRow();
        if (row >= 0) {
            selectedDoctorId = Integer.parseInt(docModel.getValueAt(row, 0).toString());
            editFName.setText(docModel.getValueAt(row, 1).toString());
            editLName.setText(docModel.getValueAt(row, 2).toString());
            editSpec.setText(docModel.getValueAt(row, 3).toString());
        }
    }

    private void saveChanges() {
        if (selectedDoctorId == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor first.");
            return;
        }
        try {
            new DoctorDao().updateDoctor(selectedDoctorId, editFName.getText(), editLName.getText(), editSpec.getText());
            JOptionPane.showMessageDialog(this, "Changes Saved!");
            loadTableData();
            loadDropdown();
            clearEdit();
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void deleteSelected() {
        if (selectedDoctorId == -1) return;
        if(JOptionPane.showConfirmDialog(this, "Delete this doctor?") == JOptionPane.YES_OPTION) {
            try {
                new DoctorDao().deleteDoctor(selectedDoctorId);
                loadTableData();
                loadDropdown();
                clearEdit();
            } catch(Exception e) {}
        }
    }

    private void clearEdit() {
        selectedDoctorId = -1;
        editFName.setText(""); editLName.setText(""); editSpec.setText("");
    }

    private void registerDoctor() {
        try {
            new DoctorDao().registerDoctor(txtFName.getText(), txtLName.getText(), txtSpec.getText(),
                    txtUser.getText(), new String(txtPass.getPassword()));
            JOptionPane.showMessageDialog(this, "Registered!");
            loadTableData(); loadDropdown();
            txtFName.setText(""); txtUser.setText("");
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void loadTableData() {
        try {
            docModel.setRowCount(0);
            for(Doctor d : new DoctorDao().getAllDoctorsList()) {
                docModel.addRow(new Object[]{d.doctorId, d.firstName, d.lastName, d.specialization});
            }
        } catch(Exception e) {}
    }

    private void loadDropdown() {
        cmbDocs.removeAllItems();
        try { for(String s : new DoctorDao().getAllDoctors()) cmbDocs.addItem(s); } catch(Exception e) {}
    }

    private void addSchedule() {
        try {
            String sel = (String) cmbDocs.getSelectedItem();
            if(sel == null) return;
            int id = Integer.parseInt(sel.split(" - ")[0]);
            new DoctorDao().addSchedule(id, cmbDay.getSelectedItem().toString(), txtStart.getText(), txtEnd.getText());
            JOptionPane.showMessageDialog(this, "Schedule Added!");
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void addRegField(JPanel p, GridBagConstraints gc, int y, String lbl, Component cmp) {
        gc.gridx=0; gc.gridy=y; p.add(new JLabel(lbl), gc);
        gc.gridx=1; p.add(cmp, gc);
    }
}