
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CourseAllocationPanel extends JPanel {

    // Services this panel uses
    private CourseAllocationService allocationService;
    private CourseService courseService;
    private TutorService tutorService;

    // -------------------------------------------------------------------------
    // INPUT FIELDS
    // -------------------------------------------------------------------------
    private JTextField txtAllocationId;
    private JComboBox<String> cmbCourseId;  // Dropdown for course IDs
    private JComboBox<String> cmbTutorId;   // Dropdown for tutor IDs
    private JTextField txtSemester;
    private JTextField txtYear;
    private JTextField txtSearch;

    // -------------------------------------------------------------------------
    // TABLE
    // -------------------------------------------------------------------------
    private JTable allocationTable;
    private DefaultTableModel tableModel;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------
    public CourseAllocationPanel(CourseAllocationService allocationService,
                                 CourseService courseService,
                                 TutorService tutorService) {
        this.allocationService = allocationService;
        this.courseService     = courseService;
        this.tutorService      = tutorService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDropdowns();
        loadAllAllocations();
    }


    private void loadDropdowns() {
        try {
            cmbCourseId.removeAllItems();
            cmbTutorId.removeAllItems();

            cmbCourseId.addItem("-- Select Course --");
            cmbTutorId.addItem("-- Select Tutor --");

            // Load all courses into dropdown
            List<Course> courses = courseService.getAllCourses();
            for (Course course : courses) {
                cmbCourseId.addItem(course.getCourseId() +
                        " - " + course.getCourseName());
            }

            // Load all tutors into dropdown
            List<Tutor> tutors = tutorService.getAllTutors();
            for (Tutor tutor : tutors) {
                cmbTutorId.addItem(tutor.getTutorId() +
                        " - " + tutor.getFirstName() +
                        " " + tutor.getLastName());
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Allocation Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Allocation ID and Course dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Allocation ID:"), gbc);
        gbc.gridx = 1;
        txtAllocationId = new JTextField(15);
        formPanel.add(txtAllocationId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 3;
        cmbCourseId = new JComboBox<>();
        cmbCourseId.setPreferredSize(new Dimension(250, 25));
        formPanel.add(cmbCourseId, gbc);

        // Row 2: Tutor dropdown and Semester
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tutor:"), gbc);
        gbc.gridx = 1;
        cmbTutorId = new JComboBox<>();
        cmbTutorId.setPreferredSize(new Dimension(250, 25));
        formPanel.add(cmbTutorId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 3;
        txtSemester = new JTextField(15);
        formPanel.add(txtSemester, gbc);

        // Row 3: Year and search
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        txtYear = new JTextField(15);
        formPanel.add(txtYear, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 3;
        txtSearch = new JTextField(15);
        formPanel.add(txtSearch, gbc);

        // Row 4: Search button, View All button and Refresh button
        gbc.gridx = 0; gbc.gridy = 3;
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchAllocations());
        formPanel.add(btnSearch, gbc);

        gbc.gridx = 1;
        JButton btnViewAll = new JButton("View All");
        btnViewAll.addActionListener(e -> loadAllAllocations());
        formPanel.add(btnViewAll, gbc);

        gbc.gridx = 2;
        JButton btnRefresh = new JButton("Refresh Course/Tutor Lists");
        btnRefresh.addActionListener(e -> loadDropdowns());
        gbc.gridwidth = 2;
        formPanel.add(btnRefresh, gbc);
        gbc.gridwidth = 1;

        return formPanel;
    }

    // -------------------------------------------------------------------------
    // createTablePanel()
    // -------------------------------------------------------------------------
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Course Allocation Records"));

        String[] columns = {
            "Allocation ID", "Course ID", "Tutor ID", "Semester", "Year"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allocationTable = new JTable(tableModel);
        allocationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        allocationTable.getTableHeader().setReorderingAllowed(false);

        // When user clicks a row fill the form with that allocation's data
        allocationTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(allocationTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // -------------------------------------------------------------------------
    // createButtonPanel()
    // -------------------------------------------------------------------------
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // Allocate Course button
        JButton btnAdd = new JButton("Allocate Course");
        btnAdd.setBackground(new Color(46, 139, 87));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addAllocation());
        buttonPanel.add(btnAdd);

        // Update Allocation button
        JButton btnUpdate = new JButton("Update Allocation");
        btnUpdate.setBackground(new Color(70, 130, 180));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateAllocation());
        buttonPanel.add(btnUpdate);

        // Delete Allocation button
        JButton btnDelete = new JButton("Delete Allocation");
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteAllocation());
        buttonPanel.add(btnDelete);

        // Clear Form button
        JButton btnClear = new JButton("Clear Form");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        return buttonPanel;
    }

    // -------------------------------------------------------------------------
    // addAllocation()
    // -------------------------------------------------------------------------
    private void addAllocation() {
        try {
            CourseAllocation allocation = getAllocationFromForm();
            allocationService.addAllocation(allocation);

            JOptionPane.showMessageDialog(this,
                    "Course allocated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllAllocations();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error allocating course: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // updateAllocation()
    // -------------------------------------------------------------------------
    private void updateAllocation() {
        try {
            CourseAllocation allocation = getAllocationFromForm();
            allocationService.updateAllocation(allocation);

            JOptionPane.showMessageDialog(this,
                    "Allocation updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadAllAllocations();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating allocation: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // deleteAllocation()
    // -------------------------------------------------------------------------
    private void deleteAllocation() {
        String allocationId = txtAllocationId.getText().trim();

        if (allocationId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an allocation to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete allocation " + allocationId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                allocationService.deleteAllocation(allocationId);

                JOptionPane.showMessageDialog(this,
                        "Allocation deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadAllAllocations();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting allocation: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------------------------------------------------------------
    // searchAllocations()
    // -------------------------------------------------------------------------
    private void searchAllocations() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadAllAllocations();
            return;
        }

        try {
            List<CourseAllocation> allocations =
                    allocationService.searchAllocations(keyword);
            populateTable(allocations);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching allocations: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // loadAllAllocations()
    // -------------------------------------------------------------------------
    private void loadAllAllocations() {
        try {
            List<CourseAllocation> allocations =
                    allocationService.getAllAllocations();
            populateTable(allocations);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading allocations: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // populateTable()
    // -------------------------------------------------------------------------
    private void populateTable(List<CourseAllocation> allocations) {
        tableModel.setRowCount(0);

        for (CourseAllocation allocation : allocations) {
            tableModel.addRow(new Object[]{
                allocation.getAllocationId(),
                allocation.getCourseId(),
                allocation.getTutorId(),
                allocation.getSemester(),
                allocation.getYear()
            });
        }
    }

    // -------------------------------------------------------------------------
    // fillFormFromTable()
    // -------------------------------------------------------------------------
    private void fillFormFromTable() {
        int selectedRow = allocationTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtAllocationId.setText(
                    tableModel.getValueAt(selectedRow, 0).toString());

            // Find and select matching course in dropdown
            String courseId = tableModel.getValueAt(selectedRow, 1).toString();
            for (int i = 0; i < cmbCourseId.getItemCount(); i++) {
                if (cmbCourseId.getItemAt(i).startsWith(courseId)) {
                    cmbCourseId.setSelectedIndex(i);
                    break;
                }
            }

            // Find and select matching tutor in dropdown
            String tutorId = tableModel.getValueAt(selectedRow, 2).toString();
            for (int i = 0; i < cmbTutorId.getItemCount(); i++) {
                if (cmbTutorId.getItemAt(i).startsWith(tutorId)) {
                    cmbTutorId.setSelectedIndex(i);
                    break;
                }
            }

            txtSemester.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtYear.setText(tableModel.getValueAt(selectedRow, 4).toString());
        }
    }


    private CourseAllocation getAllocationFromForm() throws Exception {
        String allocationId = txtAllocationId.getText().trim();

        // Extract course ID from dropdown
        String selectedCourse = (String) cmbCourseId.getSelectedItem();
        if (selectedCourse == null || selectedCourse.startsWith("--")) {
            throw new Exception("Please select a course.");
        }
        String courseId = selectedCourse.split(" - ")[0].trim();

        // Extract tutor ID from dropdown
        String selectedTutor = (String) cmbTutorId.getSelectedItem();
        if (selectedTutor == null || selectedTutor.startsWith("--")) {
            throw new Exception("Please select a tutor.");
        }
        String tutorId = selectedTutor.split(" - ")[0].trim();

        String semText  = txtSemester.getText().trim();
        String yearText = txtYear.getText().trim();

        if (allocationId.isEmpty()) {
            throw new Exception("Allocation ID is required.");
        }

        // Parse semester and year
        int semester, year;
        try {
            semester = Integer.parseInt(semText);
            year     = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            throw new Exception("Semester and Year must be numbers.");
        }

        return new CourseAllocation(allocationId, courseId,
                tutorId, semester, year);
    }

    // -------------------------------------------------------------------------
    // clearForm()
    // -------------------------------------------------------------------------
    private void clearForm() {
        txtAllocationId.setText("");
        cmbCourseId.setSelectedIndex(0);
        cmbTutorId.setSelectedIndex(0);
        txtSemester.setText("");
        txtYear.setText("");
        txtSearch.setText("");
        allocationTable.clearSelection();
    }
}