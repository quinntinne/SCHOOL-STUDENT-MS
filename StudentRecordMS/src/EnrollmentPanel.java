

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class EnrollmentPanel extends JPanel {

    // Services this panel uses
    private EnrollmentService enrollmentService;
    private StudentService studentService;
    private CourseService courseService;

    // -------------------------------------------------------------------------
    // INPUT FIELDS
    // -------------------------------------------------------------------------
    private JTextField txtEnrollmentId;
    private JComboBox<String> cmbStudentId;  // Dropdown for student IDs
    private JComboBox<String> cmbCourseId;   // Dropdown for course IDs
    private JTextField txtSemester;
    private JTextField txtYear;
    private JTextField txtEnrollmentDate;    // Format: YYYY-MM-DD
    private JTextField txtSearch;

    // -------------------------------------------------------------------------
    // TABLE
    // -------------------------------------------------------------------------
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------
    public EnrollmentPanel(EnrollmentService enrollmentService,
                           StudentService studentService,
                           CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService    = studentService;
        this.courseService     = courseService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Load student and course IDs into dropdowns
        loadDropdowns();
        loadAllEnrollments();
    }


    private void loadDropdowns() {
        try {
            // Clear existing items
            cmbStudentId.removeAllItems();
            cmbCourseId.removeAllItems();

            // Add a blank option at the top
            cmbStudentId.addItem("-- Select Student --");
            cmbCourseId.addItem("-- Select Course --");

            // Load all students and add their IDs to the dropdown
            List<Student> students = studentService.getAllStudents();
            for (Student student : students) {
                cmbStudentId.addItem(student.getStudentId() +
                        " - " + student.getFirstName() +
                        " " + student.getLastName());
            }

            // Load all courses and add their IDs to the dropdown
            List<Course> courses = courseService.getAllCourses();
            for (Course course : courses) {
                cmbCourseId.addItem(course.getCourseId() +
                        " - " + course.getCourseName());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading dropdowns: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // createFormPanel()
    // -------------------------------------------------------------------------
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Enrollment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Enrollment ID and Student ID dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Enrollment ID:"), gbc);
        gbc.gridx = 1;
        txtEnrollmentId = new JTextField(15);
        formPanel.add(txtEnrollmentId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 3;
        cmbStudentId = new JComboBox<>();
        formPanel.add(cmbStudentId, gbc);

        // Row 2: Course ID dropdown and Semester
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        cmbCourseId = new JComboBox<>();
        formPanel.add(cmbCourseId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 3;
        txtSemester = new JTextField(15);
        formPanel.add(txtSemester, gbc);

        // Row 3: Year and Enrollment Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        txtYear = new JTextField(15);
        formPanel.add(txtYear, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Enrollment Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        txtEnrollmentDate = new JTextField(15);
        formPanel.add(txtEnrollmentDate, gbc);

        // Row 4: Search bar and refresh dropdowns button
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        txtSearch = new JTextField(15);
        formPanel.add(txtSearch, gbc);

        gbc.gridx = 2;
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchEnrollments());
        formPanel.add(btnSearch, gbc);

        gbc.gridx = 3;
        JButton btnViewAll = new JButton("View All");
        btnViewAll.addActionListener(e -> loadAllEnrollments());
        formPanel.add(btnViewAll, gbc);

        // Row 5: Refresh dropdowns button
        gbc.gridx = 0; gbc.gridy = 4;
        JButton btnRefresh = new JButton("Refresh Student/Course Lists");
        btnRefresh.addActionListener(e -> loadDropdowns());
        gbc.gridwidth = 2; // Span 2 columns
        formPanel.add(btnRefresh, gbc);
        gbc.gridwidth = 1; // Reset

        return formPanel;
    }

    // -------------------------------------------------------------------------
    // createTablePanel()
    // -------------------------------------------------------------------------
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Enrollment Records"));

        String[] columns = {
            "Enrollment ID", "Student ID", "Course ID",
            "Semester", "Year", "Enrollment Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        enrollmentTable = new JTable(tableModel);
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrollmentTable.getTableHeader().setReorderingAllowed(false);

        // When user clicks a row fill the form with that enrollment's data
        enrollmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // -------------------------------------------------------------------------
    // createButtonPanel()
    // -------------------------------------------------------------------------
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // Add Enrollment button
        JButton btnAdd = new JButton("Add Enrollment");
        btnAdd.setBackground(new Color(46, 139, 87));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addEnrollment());
        buttonPanel.add(btnAdd);

        // Update Enrollment button
        JButton btnUpdate = new JButton("Update Enrollment");
        btnUpdate.setBackground(new Color(70, 130, 180));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateEnrollment());
        buttonPanel.add(btnUpdate);

        // Delete Enrollment button
        JButton btnDelete = new JButton("Delete Enrollment");
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteEnrollment());
        buttonPanel.add(btnDelete);

        // Clear Form button
        JButton btnClear = new JButton("Clear Form");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        return buttonPanel;
    }

    // -------------------------------------------------------------------------
    // addEnrollment()
    // -------------------------------------------------------------------------
    private void addEnrollment() {
        try {
            Enrollment enrollment = getEnrollmentFromForm();
            enrollmentService.addEnrollment(enrollment);

            JOptionPane.showMessageDialog(this,
                    "Enrollment added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllEnrollments();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding enrollment: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // updateEnrollment()
    // -------------------------------------------------------------------------
    private void updateEnrollment() {
        try {
            Enrollment enrollment = getEnrollmentFromForm();
            enrollmentService.updateEnrollment(enrollment);

            JOptionPane.showMessageDialog(this,
                    "Enrollment updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllEnrollments();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating enrollment: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // deleteEnrollment()
    // -------------------------------------------------------------------------
    private void deleteEnrollment() {
        String enrollmentId = txtEnrollmentId.getText().trim();

        if (enrollmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an enrollment to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete enrollment " + enrollmentId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                enrollmentService.deleteEnrollment(enrollmentId);

                JOptionPane.showMessageDialog(this,
                        "Enrollment deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadAllEnrollments();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting enrollment: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------------------------------------------------------------
    // searchEnrollments()
    // -------------------------------------------------------------------------
    private void searchEnrollments() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadAllEnrollments();
            return;
        }

        try {
            List<Enrollment> enrollments = enrollmentService.searchEnrollments(keyword);
            populateTable(enrollments);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching enrollments: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // loadAllEnrollments()
    // -------------------------------------------------------------------------
    private void loadAllEnrollments() {
        try {
            List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
            populateTable(enrollments);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading enrollments: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // populateTable()
    // -------------------------------------------------------------------------
    private void populateTable(List<Enrollment> enrollments) {
        tableModel.setRowCount(0);

        for (Enrollment enrollment : enrollments) {
            tableModel.addRow(new Object[]{
                enrollment.getEnrollmentId(),
                enrollment.getStudentId(),
                enrollment.getCourseId(),
                enrollment.getSemester(),
                enrollment.getYear(),
                enrollment.getEnrollmentDate().toString()
            });
        }
    }


    private void fillFormFromTable() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtEnrollmentId.setText(
                    tableModel.getValueAt(selectedRow, 0).toString());

            // Find and select the matching student in the dropdown
            String studentId = tableModel.getValueAt(selectedRow, 1).toString();
            for (int i = 0; i < cmbStudentId.getItemCount(); i++) {
                if (cmbStudentId.getItemAt(i).startsWith(studentId)) {
                    cmbStudentId.setSelectedIndex(i);
                    break;
                }
            }

            // Find and select the matching course in the dropdown
            String courseId = tableModel.getValueAt(selectedRow, 2).toString();
            for (int i = 0; i < cmbCourseId.getItemCount(); i++) {
                if (cmbCourseId.getItemAt(i).startsWith(courseId)) {
                    cmbCourseId.setSelectedIndex(i);
                    break;
                }
            }

            txtSemester.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtYear.setText(tableModel.getValueAt(selectedRow, 4).toString());
            txtEnrollmentDate.setText(
                    tableModel.getValueAt(selectedRow, 5).toString());
        }
    }

    // -------------------------------------------------------------------------
    // getEnrollmentFromForm()
    // Reads all form fields and creates an Enrollment object
    // -------------------------------------------------------------------------
    private Enrollment getEnrollmentFromForm() throws Exception {
        String enrollmentId = txtEnrollmentId.getText().trim();

        // Extract just the ID part from the dropdown
        // e.g. "CHUKA/COMP/2023/001 - Alice Smith" → "CHUKA/COMP/2023/001"
        String selectedStudent = (String) cmbStudentId.getSelectedItem();
        String selectedCourse  = (String) cmbCourseId.getSelectedItem();

        if (selectedStudent == null || selectedStudent.startsWith("--") ||
                selectedCourse == null || selectedCourse.startsWith("--")) {
            throw new Exception("Please select a student and a course.");
        }

        // Extract just the ID before the " - " separator
        String studentId = selectedStudent.split(" - ")[0].trim();
        String courseId  = selectedCourse.split(" - ")[0].trim();

        String semText  = txtSemester.getText().trim();
        String yearText = txtYear.getText().trim();
        String dateText = txtEnrollmentDate.getText().trim();

        if (enrollmentId.isEmpty()) {
            throw new Exception("Enrollment ID is required.");
        }

        // Parse semester and year
        int semester, year;
        try {
            semester = Integer.parseInt(semText);
            year     = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            throw new Exception("Semester and Year must be numbers.");
        }

        // Parse enrollment date
        LocalDate enrollmentDate;
        try {
            enrollmentDate = LocalDate.parse(dateText);
        } catch (Exception e) {
            throw new Exception("Invalid date format. Please use YYYY-MM-DD.");
        }

        return new Enrollment(enrollmentId, studentId, courseId,
                semester, year, enrollmentDate);
    }

    // -------------------------------------------------------------------------
    // clearForm()
    // -------------------------------------------------------------------------
    private void clearForm() {
        txtEnrollmentId.setText("");
        cmbStudentId.setSelectedIndex(0);
        cmbCourseId.setSelectedIndex(0);
        txtSemester.setText("");
        txtYear.setText("");
        txtEnrollmentDate.setText("");
        txtSearch.setText("");
        enrollmentTable.clearSelection();
    }
}