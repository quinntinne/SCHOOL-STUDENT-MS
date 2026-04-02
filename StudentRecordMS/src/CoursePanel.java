
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CoursePanel extends JPanel {

    // Service this panel uses
    private CourseService courseService;

    // -------------------------------------------------------------------------
    // INPUT FIELDS
    // -------------------------------------------------------------------------
    private JTextField txtCourseId;
    private JTextField txtCourseName;
    private JTextField txtCredits;
    private JTextField txtDepartment;
    private JTextField txtSearch;

    // -------------------------------------------------------------------------
    // TABLE
    // -------------------------------------------------------------------------
    private JTable courseTable;
    private DefaultTableModel tableModel;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------
    public CoursePanel(CourseService courseService) {
        this.courseService = courseService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadAllCourses();
    }

    // -------------------------------------------------------------------------
    // createFormPanel()
    // Creates the input form at the top
    // -------------------------------------------------------------------------
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Course ID and Course Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Course ID:"), gbc);
        gbc.gridx = 1;
        txtCourseId = new JTextField(15);
        formPanel.add(txtCourseId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Course Name:"), gbc);
        gbc.gridx = 3;
        txtCourseName = new JTextField(15);
        formPanel.add(txtCourseName, gbc);

        // Row 2: Credits and Department
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        txtCredits = new JTextField(15);
        formPanel.add(txtCredits, gbc);

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
        btnSearch.addActionListener(e -> searchCourses());
        formPanel.add(btnSearch, gbc);

        gbc.gridx = 3;
        JButton btnViewAll = new JButton("View All");
        btnViewAll.addActionListener(e -> loadAllCourses());
        formPanel.add(btnViewAll, gbc);

        return formPanel;
    }

    // -------------------------------------------------------------------------
    // createTablePanel()
    // Creates the table that displays course records
    // -------------------------------------------------------------------------
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Course Records"));

        // Define column headers
        String[] columns = {"Course ID", "Course Name", "Credits", "Department"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is read only
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getTableHeader().setReorderingAllowed(false);

        // When user clicks a row fill the form with that course's data
        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // -------------------------------------------------------------------------
    // createButtonPanel()
    // Creates the row of action buttons
    // -------------------------------------------------------------------------
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // Add Course button
        JButton btnAdd = new JButton("Add Course");
        btnAdd.setBackground(new Color(46, 139, 87));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addCourse());
        buttonPanel.add(btnAdd);

        // Update Course button
        JButton btnUpdate = new JButton("Update Course");
        btnUpdate.setBackground(new Color(70, 130, 180));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateCourse());
        buttonPanel.add(btnUpdate);

        // Delete Course button
        JButton btnDelete = new JButton("Delete Course");
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteCourse());
        buttonPanel.add(btnDelete);

        // Clear Form button
        JButton btnClear = new JButton("Clear Form");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        return buttonPanel;
    }

    // -------------------------------------------------------------------------
    // addCourse()
    // Reads form data and adds a new course
    // -------------------------------------------------------------------------
    private void addCourse() {
        try {
            Course course = getCourseFromForm();
            courseService.addCourse(course);

            JOptionPane.showMessageDialog(this,
                    "Course added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllCourses();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding course: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // updateCourse()
    // Updates the currently selected course
    // -------------------------------------------------------------------------
    private void updateCourse() {
        try {
            Course course = getCourseFromForm();
            courseService.updateCourse(course);

            JOptionPane.showMessageDialog(this,
                    "Course updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllCourses();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating course: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // deleteCourse()
    // Deletes the course whose ID is in the Course ID field
    // -------------------------------------------------------------------------
    private void deleteCourse() {
        String courseId = txtCourseId.getText().trim();

        if (courseId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm before deleting
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete course " + courseId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                courseService.deleteCourse(courseId);

                JOptionPane.showMessageDialog(this,
                        "Course deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadAllCourses();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting course: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------------------------------------------------------------
    // searchCourses()
    // Searches courses by keyword
    // -------------------------------------------------------------------------
    private void searchCourses() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadAllCourses();
            return;
        }

        try {
            List<Course> courses = courseService.searchCourses(keyword);
            populateTable(courses);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching courses: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // loadAllCourses()
    // Loads all courses and displays them in the table
    // -------------------------------------------------------------------------
    private void loadAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            populateTable(courses);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // populateTable()
    // Fills the table with a list of courses
    // -------------------------------------------------------------------------
    private void populateTable(List<Course> courses) {
        tableModel.setRowCount(0); // Clear existing rows

        for (Course course : courses) {
            tableModel.addRow(new Object[]{
                course.getCourseId(),
                course.getCourseName(),
                course.getCredits(),
                course.getDepartment()
            });
        }
    }

    // -------------------------------------------------------------------------
    // fillFormFromTable()
    // Fills form fields when user clicks a row in the table
    // -------------------------------------------------------------------------
    private void fillFormFromTable() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtCourseId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtCourseName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtCredits.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtDepartment.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }

    // -------------------------------------------------------------------------
    // getCourseFromForm()
    // Reads all form fields and creates a Course object
    // -------------------------------------------------------------------------
    private Course getCourseFromForm() throws Exception {
        String courseId   = txtCourseId.getText().trim();
        String courseName = txtCourseName.getText().trim();
        String credText   = txtCredits.getText().trim();
        String department = txtDepartment.getText().trim();

        if (courseId.isEmpty() || courseName.isEmpty()) {
            throw new Exception("Course ID and Course Name are required.");
        }

        // Parse credits as a number
        int credits;
        try {
            credits = Integer.parseInt(credText);
        } catch (NumberFormatException e) {
            throw new Exception("Credits must be a number.");
        }

        return new Course(courseId, courseName, credits, department);
    }

    // -------------------------------------------------------------------------
    // clearForm()
    // Clears all input fields
    // -------------------------------------------------------------------------
    private void clearForm() {
        txtCourseId.setText("");
        txtCourseName.setText("");
        txtCredits.setText("");
        txtDepartment.setText("");
        txtSearch.setText("");
        courseTable.clearSelection();
    }
}