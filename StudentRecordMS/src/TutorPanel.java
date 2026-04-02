
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TutorPanel extends JPanel {

    private TutorService tutorService;

    // -------------------------------------------------------------------------
    // INPUT FIELDS
    // -------------------------------------------------------------------------
    private JTextField txtTutorId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtDepartment;
    private JTextField txtSearch;

    // -------------------------------------------------------------------------
    // TABLE
    // -------------------------------------------------------------------------
    private JTable tutorTable;
    private DefaultTableModel tableModel;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------
    public TutorPanel(TutorService tutorService) {
        this.tutorService = tutorService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadAllTutors();
    }

    // -------------------------------------------------------------------------
    // createFormPanel()
    // -------------------------------------------------------------------------
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tutor Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Tutor ID and First Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tutor ID:"), gbc);
        gbc.gridx = 1;
        txtTutorId = new JTextField(15);
        formPanel.add(txtTutorId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 3;
        txtFirstName = new JTextField(15);
        formPanel.add(txtFirstName, gbc);

        // Row 2: Last Name and Department
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        txtLastName = new JTextField(15);
        formPanel.add(txtLastName, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 3;
        txtDepartment = new JTextField(15);
        formPanel.add(txtDepartment, gbc);

        // Row 3: Search bar
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        txtSearch = new JTextField(15);
        formPanel.add(txtSearch, gbc);

        gbc.gridx = 2;
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchTutors());
        formPanel.add(btnSearch, gbc);

        gbc.gridx = 3;
        JButton btnViewAll = new JButton("View All");
        btnViewAll.addActionListener(e -> loadAllTutors());
        formPanel.add(btnViewAll, gbc);

        return formPanel;
    }

    // -------------------------------------------------------------------------
    // createTablePanel()
    // -------------------------------------------------------------------------
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Tutor Records"));

        String[] columns = {"Tutor ID", "First Name", "Last Name", "Department"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tutorTable = new JTable(tableModel);
        tutorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tutorTable.getTableHeader().setReorderingAllowed(false);

        tutorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tutorTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // -------------------------------------------------------------------------
    // createButtonPanel()
    // -------------------------------------------------------------------------
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton btnAdd = new JButton("Add Tutor");
        btnAdd.setBackground(new Color(46, 139, 87));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addTutor());
        buttonPanel.add(btnAdd);

        JButton btnUpdate = new JButton("Update Tutor");
        btnUpdate.setBackground(new Color(70, 130, 180));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateTutor());
        buttonPanel.add(btnUpdate);

        JButton btnDelete = new JButton("Delete Tutor");
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteTutor());
        buttonPanel.add(btnDelete);

        JButton btnClear = new JButton("Clear Form");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        return buttonPanel;
    }

    // -------------------------------------------------------------------------
    // addTutor()
    // -------------------------------------------------------------------------
    private void addTutor() {
        try {
            Tutor tutor = getTutorFromForm();
            tutorService.addTutor(tutor);

            JOptionPane.showMessageDialog(this,
                    "Tutor added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllTutors();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding tutor: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // updateTutor()
    // -------------------------------------------------------------------------
    private void updateTutor() {
        try {
            Tutor tutor = getTutorFromForm();
            tutorService.updateTutor(tutor);

            JOptionPane.showMessageDialog(this,
                    "Tutor updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllTutors();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating tutor: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // deleteTutor()
    // -------------------------------------------------------------------------
    private void deleteTutor() {
        String tutorId = txtTutorId.getText().trim();

        if (tutorId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a tutor to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete tutor " + tutorId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tutorService.deleteTutor(tutorId);

                JOptionPane.showMessageDialog(this,
                        "Tutor deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadAllTutors();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting tutor: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------------------------------------------------------------
    // searchTutors()
    // -------------------------------------------------------------------------
    private void searchTutors() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadAllTutors();
            return;
        }

        try {
            List<Tutor> tutors = tutorService.searchTutors(keyword);
            populateTable(tutors);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching tutors: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // loadAllTutors()
    // -------------------------------------------------------------------------
    private void loadAllTutors() {
        try {
            List<Tutor> tutors = tutorService.getAllTutors();
            populateTable(tutors);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading tutors: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // populateTable()
    // -------------------------------------------------------------------------
    private void populateTable(List<Tutor> tutors) {
        tableModel.setRowCount(0);

        for (Tutor tutor : tutors) {
            tableModel.addRow(new Object[]{
                tutor.getTutorId(),
                tutor.getFirstName(),
                tutor.getLastName(),
                tutor.getDepartment()
            });
        }
    }

    // -------------------------------------------------------------------------
    // fillFormFromTable()
    // -------------------------------------------------------------------------
    private void fillFormFromTable() {
        int selectedRow = tutorTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtTutorId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtFirstName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtLastName.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtDepartment.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }

    // -------------------------------------------------------------------------
    // getTutorFromForm()
    // -------------------------------------------------------------------------
    private Tutor getTutorFromForm() throws Exception {
        String tutorId    = txtTutorId.getText().trim();
        String firstName  = txtFirstName.getText().trim();
        String lastName   = txtLastName.getText().trim();
        String department = txtDepartment.getText().trim();

        if (tutorId.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            throw new Exception("Tutor ID, First Name and Last Name are required.");
        }

        return new Tutor(tutorId, firstName, lastName, department);
    }

    // -------------------------------------------------------------------------
    // clearForm()
    // -------------------------------------------------------------------------
    private void clearForm() {
        txtTutorId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtDepartment.setText("");
        txtSearch.setText("");
        tutorTable.clearSelection();
    }
}