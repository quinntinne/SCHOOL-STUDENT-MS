

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class StudentPanel extends JPanel {

    // Services this panel uses
    private StudentService studentService;
    private AcademicProgressionService progressionService;

    // -------------------------------------------------------------------------
    // INPUT FIELDS — where the user types student information
    // -------------------------------------------------------------------------
    private JTextField txtStudentId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtDateOfBirth; // Format: YYYY-MM-DD
    private JTextField txtMajor;
    private JTextField txtSemester;
    private JTextField txtYear;
    private JComboBox<String> cmbStatus;
    private JTextField txtSearch;

    // -------------------------------------------------------------------------
    // TABLE — displays the list of students
    // -------------------------------------------------------------------------
    private JTable studentTable;
    private DefaultTableModel tableModel;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------
    public StudentPanel(StudentService studentService,
                        AcademicProgressionService progressionService) {
        this.studentService = studentService;
        this.progressionService = progressionService;

        // Use BorderLayout for the overall panel
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Build each section of the panel
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Load and display all students when the panel first opens
        loadAllStudents();
    }

    // -------------------------------------------------------------------------
    // createFormPanel()
    // Creates the input form at the top where users type student details
    // -------------------------------------------------------------------------
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around each element
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Student ID and First Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        txtStudentId = new JTextField(15);
        formPanel.add(txtStudentId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 3;
        txtFirstName = new JTextField(15);
        formPanel.add(txtFirstName, gbc);

        // Row 2: Last Name and Date of Birth
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        txtLastName = new JTextField(15);
        formPanel.add(txtLastName, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        txtDateOfBirth = new JTextField(15);
        formPanel.add(txtDateOfBirth, gbc);

        // Row 3: Major and Status
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Major:"), gbc);
        gbc.gridx = 1;
        txtMajor = new JTextField(15);
        formPanel.add(txtMajor, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Academic Status:"), gbc);
        gbc.gridx = 3;
        // Dropdown for academic status
        cmbStatus = new JComboBox<>(new String[]{
            "Active", "On Probation", "Cleared for Graduation", "Graduated"
        });
        formPanel.add(cmbStatus, gbc);

        // Row 4: Semester and Year
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Current Semester:"), gbc);
        gbc.gridx = 1;
        txtSemester = new JTextField(15);
        formPanel.add(txtSemester, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Current Year:"), gbc);
        gbc.gridx = 3;
        txtYear = new JTextField(15);
        formPanel.add(txtYear, gbc);

        // Row 5: Search bar
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        txtSearch = new JTextField(15);
        formPanel.add(txtSearch, gbc);

        gbc.gridx = 2;
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchStudents());
        formPanel.add(btnSearch, gbc);

        gbc.gridx = 3;
        JButton btnViewAll = new JButton("View All");
        btnViewAll.addActionListener(e -> loadAllStudents());
        formPanel.add(btnViewAll, gbc);

        return formPanel;
    }

    // -------------------------------------------------------------------------
    // createTablePanel()
    // Creates the table in the middle that displays student records
    // -------------------------------------------------------------------------
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Student Records"));

        // Define the column headers for the table
        String[] columns = {
            "Student ID", "First Name", "Last Name",
            "Date of Birth", "Major", "Semester", "Year", "Status"
        };

        // Create the table model — this holds all the data
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only — users edit via the form
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setReorderingAllowed(false);

        // When user clicks a row in the table,
        // fill the form fields with that student's data
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });

        // Put the table in a scroll pane so it can scroll if there are many students
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // -------------------------------------------------------------------------
    // createButtonPanel()
    // Creates the row of action buttons at the bottom
    // -------------------------------------------------------------------------
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // Add Student button
        JButton btnAdd = new JButton("Add Student");
        btnAdd.setBackground(new Color(46, 139, 87)); // Green
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addStudent());
        buttonPanel.add(btnAdd);

        // Update Student button
        JButton btnUpdate = new JButton("Update Student");
        btnUpdate.setBackground(new Color(70, 130, 180)); // Blue
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateStudent());
        buttonPanel.add(btnUpdate);

        // Delete Student button
        JButton btnDelete = new JButton("Delete Student");
        btnDelete.setBackground(new Color(178, 34, 34)); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteStudent());
        buttonPanel.add(btnDelete);

        // Promote Student button
        JButton btnPromote = new JButton("Promote to Next Class");
        btnPromote.setBackground(new Color(255, 140, 0)); // Orange
        btnPromote.setForeground(Color.WHITE);
        btnPromote.addActionListener(e -> promoteStudent());
        buttonPanel.add(btnPromote);

        // Clear for Graduation button
        JButton btnGraduate = new JButton("Clear for Graduation");
        btnGraduate.setBackground(new Color(128, 0, 128)); // Purple
        btnGraduate.setForeground(Color.WHITE);
        btnGraduate.addActionListener(e -> clearForGraduation());
        buttonPanel.add(btnGraduate);

        // Clear Form button
        JButton btnClear = new JButton("Clear Form");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        return buttonPanel;
    }

    // -------------------------------------------------------------------------
    // addStudent()
    // Reads data from the form fields and adds a new student
    // -------------------------------------------------------------------------
    private void addStudent() {
        try {
            // Read all values from the form
            Student student = getStudentFromForm();

            // Call the service to add the student
            studentService.addStudent(student);

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Student added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();        // Clear the form
            loadAllStudents();  // Refresh the table

        } catch (Exception ex) {
            // Show error message if something went wrong
            JOptionPane.showMessageDialog(this,
                    "Error adding student: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // updateStudent()
    // Updates the currently selected student with new form data
    // -------------------------------------------------------------------------
    private void updateStudent() {
        try {
            Student student = getStudentFromForm();
            studentService.updateStudent(student);

            JOptionPane.showMessageDialog(this,
                    "Student updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllStudents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating student: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // deleteStudent()
    // Deletes the student whose ID is in the Student ID field
    // -------------------------------------------------------------------------
    private void deleteStudent() {
        String studentId = txtStudentId.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ask for confirmation before deleting
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student " + studentId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                studentService.deleteStudent(studentId);

                JOptionPane.showMessageDialog(this,
                        "Student deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadAllStudents();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting student: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------------------------------------------------------------
    // promoteStudent()
    // Promotes the selected student to the next semester or year
    // -------------------------------------------------------------------------
    private void promoteStudent() {
        String studentId = txtStudentId.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to promote.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            progressionService.promoteStudent(studentId);

            JOptionPane.showMessageDialog(this,
                    "Student " + studentId + " has been promoted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllStudents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Promotion Failed", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void clearForGraduation() {
        String studentId = txtStudentId.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to clear for graduation.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            progressionService.clearForGraduation(studentId);

            JOptionPane.showMessageDialog(this,
                    "Student " + studentId + " has been cleared for graduation!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllStudents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Graduation Clearance Failed", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void searchStudents() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadAllStudents();
            return;
        }

        try {
            List<Student> students = studentService.searchStudents(keyword);
            populateTable(students);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching students: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            populateTable(students);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // populateTable()
    // Clears the table and fills it with the given list of students
    // -------------------------------------------------------------------------
    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0); // Clear existing rows

        for (Student student : students) {
            // Add each student as a new row in the table
            tableModel.addRow(new Object[]{
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName(),
                student.getDateOfBirth().toString(),
                student.getMajor(),
                student.getCurrentSemester(),
                student.getCurrentYear(),
                student.getAcademicStatus()
            });
        }
    }


    private void fillFormFromTable() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtStudentId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtFirstName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtLastName.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtDateOfBirth.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtMajor.setText(tableModel.getValueAt(selectedRow, 4).toString());
            txtSemester.setText(tableModel.getValueAt(selectedRow, 5).toString());
            txtYear.setText(tableModel.getValueAt(selectedRow, 6).toString());
            cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 7).toString());
        }
    }


    private Student getStudentFromForm() throws Exception {
        String studentId  = txtStudentId.getText().trim();
        String firstName  = txtFirstName.getText().trim();
        String lastName   = txtLastName.getText().trim();
        String dobText    = txtDateOfBirth.getText().trim();
        String major      = txtMajor.getText().trim();
        String semText    = txtSemester.getText().trim();
        String yearText   = txtYear.getText().trim();
        String status     = cmbStatus.getSelectedItem().toString();

        // Validate that required fields are not empty
        if (studentId.isEmpty() || firstName.isEmpty() ||
                lastName.isEmpty() || dobText.isEmpty()) {
            throw new Exception("Student ID, First Name, Last Name " +
                                "and Date of Birth are required.");
        }

        // Parse the date — must be in YYYY-MM-DD format
        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(dobText);
        } catch (Exception e) {
            throw new Exception("Invalid date format. Please use YYYY-MM-DD.");
        }

        // Parse semester and year as numbers
        int semester, year;
        try {
            semester = Integer.parseInt(semText);
            year     = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            throw new Exception("Semester and Year must be numbers.");
        }

        return new Student(studentId, firstName, lastName,
                dateOfBirth, major, semester, year, status);
    }


    private void clearForm() {
        txtStudentId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtDateOfBirth.setText("");
        txtMajor.setText("");
        txtSemester.setText("");
        txtYear.setText("");
        cmbStatus.setSelectedIndex(0);
        txtSearch.setText("");
        studentTable.clearSelection();
    }
}