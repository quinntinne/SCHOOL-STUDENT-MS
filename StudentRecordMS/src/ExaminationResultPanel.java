
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ExaminationResultPanel extends JPanel {

    // Services this panel uses
    private ExaminationResultService resultService;
    private EnrollmentService enrollmentService;

    // -------------------------------------------------------------------------
    // INPUT FIELDS
    // -------------------------------------------------------------------------
    private JTextField txtResultId;
    private JComboBox<String> cmbEnrollmentId; // Dropdown for enrollment IDs
    private JTextField txtScore;
    private JTextField txtGrade;               // Auto-filled based on score
    private JTextField txtSemester;
    private JTextField txtYear;
    private JTextField txtSearch;

    // -------------------------------------------------------------------------
    // TABLE
    // -------------------------------------------------------------------------
    private JTable resultTable;
    private DefaultTableModel tableModel;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------
    public ExaminationResultPanel(ExaminationResultService resultService,
                                  EnrollmentService enrollmentService) {
        this.resultService     = resultService;
        this.enrollmentService = enrollmentService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDropdowns();
        loadAllResults();
    }

    private void loadDropdowns() {
        try {
            cmbEnrollmentId.removeAllItems();
            cmbEnrollmentId.addItem("-- Select Enrollment --");

            List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
            for (Enrollment enrollment : enrollments) {
                // Show enrollment ID with student and course for easy identification
                cmbEnrollmentId.addItem(enrollment.getEnrollmentId() +
                        " (Student: " + enrollment.getStudentId() +
                        " | Course: " + enrollment.getCourseId() + ")");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading enrollments: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // createFormPanel()
    // -------------------------------------------------------------------------
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Examination Result Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Result ID and Enrollment dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Result ID:"), gbc);
        gbc.gridx = 1;
        txtResultId = new JTextField(15);
        formPanel.add(txtResultId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Enrollment:"), gbc);
        gbc.gridx = 3;
        cmbEnrollmentId = new JComboBox<>();
        cmbEnrollmentId.setPreferredSize(new Dimension(300, 25));
        formPanel.add(cmbEnrollmentId, gbc);

        // Row 2: Score and Grade
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Score (0-100):"), gbc);
        gbc.gridx = 1;
        txtScore = new JTextField(15);

        // When user types a score automatically calculate and show the grade
        txtScore.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                autoCalculateGrade();
            }
        });
        formPanel.add(txtScore, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Grade (Auto-calculated):"), gbc);
        gbc.gridx = 3;
        txtGrade = new JTextField(15);
        txtGrade.setEditable(false); // Grade is read-only — calculated from score
        txtGrade.setBackground(new Color(240, 240, 240)); // Grey to show it is read-only
        formPanel.add(txtGrade, gbc);

        // Row 3: Semester and Year
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        txtSemester = new JTextField(15);
        formPanel.add(txtSemester, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 3;
        txtYear = new JTextField(15);
        formPanel.add(txtYear, gbc);

        // Row 4: Search and refresh
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        txtSearch = new JTextField(15);
        formPanel.add(txtSearch, gbc);

        gbc.gridx = 2;
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchResults());
        formPanel.add(btnSearch, gbc);

        gbc.gridx = 3;
        JButton btnViewAll = new JButton("View All");
        btnViewAll.addActionListener(e -> loadAllResults());
        formPanel.add(btnViewAll, gbc);

        // Row 5: Refresh enrollments button
        gbc.gridx = 0; gbc.gridy = 4;
        JButton btnRefresh = new JButton("Refresh Enrollment List");
        btnRefresh.addActionListener(e -> loadDropdowns());
        gbc.gridwidth = 2;
        formPanel.add(btnRefresh, gbc);
        gbc.gridwidth = 1;

        return formPanel;
    }


    private void autoCalculateGrade() {
        try {
            String scoreText = txtScore.getText().trim();
            if (!scoreText.isEmpty()) {
                int score = Integer.parseInt(scoreText);
                // Use the calculateGrade method from ExaminationResult class
                String grade = ExaminationResult.calculateGrade(score);
                txtGrade.setText(grade);
            } else {
                txtGrade.setText(""); // Clear grade if score is empty
            }
        } catch (NumberFormatException e) {
            txtGrade.setText(""); // Clear grade if score is not a valid number
        }
    }

    // -------------------------------------------------------------------------
    // createTablePanel()
    // -------------------------------------------------------------------------
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Examination Results"));

        String[] columns = {
            "Result ID", "Enrollment ID", "Score", "Grade", "Semester", "Year"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.getTableHeader().setReorderingAllowed(false);

        // Colour code rows based on pass/fail
        // Green for pass (score >= 40), Red for fail (score < 40)
        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    // Get the score from column 2
                    Object scoreObj = table.getModel().getValueAt(row, 2);
                    if (scoreObj != null) {
                        int score = Integer.parseInt(scoreObj.toString());
                        if (score >= 40) {
                            c.setBackground(new Color(198, 239, 206)); // Light green
                        } else {
                            c.setBackground(new Color(255, 199, 206)); // Light red
                        }
                    }
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                return c;
            }
        });

        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // -------------------------------------------------------------------------
    // createButtonPanel()
    // -------------------------------------------------------------------------
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // Add Result button
        JButton btnAdd = new JButton("Add Result");
        btnAdd.setBackground(new Color(46, 139, 87));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addResult());
        buttonPanel.add(btnAdd);

        // Update Result button
        JButton btnUpdate = new JButton("Update Result");
        btnUpdate.setBackground(new Color(70, 130, 180));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateResult());
        buttonPanel.add(btnUpdate);

        // Delete Result button
        JButton btnDelete = new JButton("Delete Result");
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteResult());
        buttonPanel.add(btnDelete);

        // Clear Form button
        JButton btnClear = new JButton("Clear Form");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        return buttonPanel;
    }

    // -------------------------------------------------------------------------
    // addResult()
    // -------------------------------------------------------------------------
    private void addResult() {
        try {
            ExaminationResult result = getResultFromForm();
            resultService.addResult(result);

            JOptionPane.showMessageDialog(this,
                    "Result added successfully! Grade: " + result.getGrade(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllResults();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding result: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // updateResult()
    // -------------------------------------------------------------------------
    private void updateResult() {
        try {
            ExaminationResult result = getResultFromForm();
            resultService.updateResult(result);

            JOptionPane.showMessageDialog(this,
                    "Result updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllResults();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating result: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // deleteResult()
    // -------------------------------------------------------------------------
    private void deleteResult() {
        String resultId = txtResultId.getText().trim();

        if (resultId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a result to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete result " + resultId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                resultService.deleteResult(resultId);

                JOptionPane.showMessageDialog(this,
                        "Result deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadAllResults();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting result: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------------------------------------------------------------
    // searchResults()
    // -------------------------------------------------------------------------
    private void searchResults() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadAllResults();
            return;
        }

        try {
            List<ExaminationResult> results = resultService.searchResults(keyword);
            populateTable(results);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching results: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // loadAllResults()
    // -------------------------------------------------------------------------
    private void loadAllResults() {
        try {
            List<ExaminationResult> results = resultService.getAllResults();
            populateTable(results);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading results: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // populateTable()
    // -------------------------------------------------------------------------
    private void populateTable(List<ExaminationResult> results) {
        tableModel.setRowCount(0);

        for (ExaminationResult result : results) {
            tableModel.addRow(new Object[]{
                result.getResultId(),
                result.getEnrollmentId(),
                result.getScore(),
                result.getGrade(),
                result.getSemester(),
                result.getYear()
            });
        }
    }

    // -------------------------------------------------------------------------
    // fillFormFromTable()
    // -------------------------------------------------------------------------
    private void fillFormFromTable() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtResultId.setText(
                    tableModel.getValueAt(selectedRow, 0).toString());

            // Find and select matching enrollment in dropdown
            String enrollmentId = tableModel.getValueAt(selectedRow, 1).toString();
            for (int i = 0; i < cmbEnrollmentId.getItemCount(); i++) {
                if (cmbEnrollmentId.getItemAt(i).startsWith(enrollmentId)) {
                    cmbEnrollmentId.setSelectedIndex(i);
                    break;
                }
            }

            txtScore.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtGrade.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtSemester.setText(tableModel.getValueAt(selectedRow, 4).toString());
            txtYear.setText(tableModel.getValueAt(selectedRow, 5).toString());
        }
    }

    // -------------------------------------------------------------------------
    // getResultFromForm()
    // Reads all form fields and creates an ExaminationResult object
    // -------------------------------------------------------------------------
    private ExaminationResult getResultFromForm() throws Exception {
        String resultId = txtResultId.getText().trim();

        // Extract enrollment ID from dropdown
        String selectedEnrollment = (String) cmbEnrollmentId.getSelectedItem();
        if (selectedEnrollment == null || selectedEnrollment.startsWith("--")) {
            throw new Exception("Please select an enrollment.");
        }

        // Extract just the enrollment ID before the space
        String enrollmentId = selectedEnrollment.split(" ")[0].trim();

        String scoreText = txtScore.getText().trim();
        String semText   = txtSemester.getText().trim();
        String yearText  = txtYear.getText().trim();

        if (resultId.isEmpty()) {
            throw new Exception("Result ID is required.");
        }

        // Parse score
        int score;
        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException e) {
            throw new Exception("Score must be a number between 0 and 100.");
        }

        // Parse semester and year
        int semester, year;
        try {
            semester = Integer.parseInt(semText);
            year     = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            throw new Exception("Semester and Year must be numbers.");
        }

        // Grade will be auto-calculated by the service
        // We pass an empty string here — the service will replace it
        String grade = ExaminationResult.calculateGrade(score);

        return new ExaminationResult(resultId, enrollmentId,
                score, grade, semester, year);
    }

    // -------------------------------------------------------------------------
    // clearForm()
    // -------------------------------------------------------------------------
    private void clearForm() {
        txtResultId.setText("");
        cmbEnrollmentId.setSelectedIndex(0);
        txtScore.setText("");
        txtGrade.setText("");
        txtSemester.setText("");
        txtYear.setText("");
        txtSearch.setText("");
        resultTable.clearSelection();
    }
}