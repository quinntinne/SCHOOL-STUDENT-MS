
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    // -------------------------------------------------------------------------
    // These are the DAOs — we declare them here so we can switch between
    // file mode and database mode from the menu
    // -------------------------------------------------------------------------
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private TutorDAO tutorDAO;
    private EnrollmentDAO enrollmentDAO;
    private ExaminationResultDAO resultDAO;
    private CourseAllocationDAO allocationDAO;

    // -------------------------------------------------------------------------
    // These are the Service classes
    // -------------------------------------------------------------------------
    private StudentService studentService;
    private CourseService courseService;
    private TutorService tutorService;
    private EnrollmentService enrollmentService;
    private ExaminationResultService resultService;
    private CourseAllocationService allocationService;
    private AcademicProgressionService progressionService;
    private ReportService reportService;

    // The tabbed panel that holds all the different sections
    private JTabbedPane tabbedPane;

    // A label at the bottom showing which mode we are in (File or Database)
    private JLabel statusLabel;

    // Tracks whether we are using file mode or database mode
    // true = database mode, false = file mode
    private boolean isDatabaseMode = false;


    public MainFrame() {
        // Set up the window properties
        setTitle("Chuka University - Student Records Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app when window closes
        setLocationRelativeTo(null); // Center the window on screen

        // Start in FILE mode by default
        initializeFileMode();

        // Build the menu bar at the top
        createMenuBar();

        // Build the main content area with all tabs
        createMainContent();

        // Build the status bar at the bottom
        createStatusBar();
    }


    private void initializeFileMode() {
        // Create file-based DAOs
        studentDAO      = new StudentFileDAO();
        courseDAO       = new CourseFileDAO();
        tutorDAO        = new TutorFileDAO();
        enrollmentDAO   = new EnrollmentFileDAO();
        resultDAO       = new ExaminationResultFileDAO();
        allocationDAO   = new CourseAllocationFileDAO();

        // Create services using those DAOs
        initializeServices();
        isDatabaseMode = false;
    }


    private void initializeDatabaseMode() {
        // Create database-based DAOs
        studentDAO      = new StudentDatabaseDAO();
        courseDAO       = new CourseDatabaseDAO();
        tutorDAO        = new TutorDatabaseDAO();
        enrollmentDAO   = new EnrollmentDatabaseDAO();
        resultDAO       = new ExaminationResultDatabaseDAO();
        allocationDAO   = new CourseAllocationDatabaseDAO();

        // Create services using those DAOs
        initializeServices();
        isDatabaseMode = true;
    }


    private void initializeServices() {
        studentService    = new StudentService(studentDAO);
        courseService     = new CourseService(courseDAO);
        tutorService      = new TutorService(tutorDAO);
        enrollmentService = new EnrollmentService(enrollmentDAO);
        resultService     = new ExaminationResultService(resultDAO);
        allocationService = new CourseAllocationService(allocationDAO);
        progressionService = new AcademicProgressionService(
                studentDAO, studentService, enrollmentService);
        reportService     = new ReportService(studentDAO, courseDAO, tutorDAO,
                enrollmentDAO, resultDAO, allocationDAO);
    }


    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // "Settings" menu
        JMenu settingsMenu = new JMenu("Settings");

        // Option to switch to File mode
        JMenuItem fileModeItem = new JMenuItem("Use File Storage");
        fileModeItem.addActionListener(e -> {
            initializeFileMode();         // Switch to file DAOs
            refreshTabs();                // Rebuild all tabs with new services
            statusLabel.setText("Mode: File Storage"); // Update status bar
            JOptionPane.showMessageDialog(this,
                    "Switched to File Storage mode.",
                    "Mode Changed", JOptionPane.INFORMATION_MESSAGE);
        });

        // Option to switch to Database mode
        JMenuItem dbModeItem = new JMenuItem("Use Database Storage");
        dbModeItem.addActionListener(e -> {
            try {
                // Test the database connection first
                DatabaseConnection.getConnection();
                initializeDatabaseMode();     // Switch to database DAOs
                refreshTabs();                // Rebuild all tabs
                statusLabel.setText("Mode: Database Storage");
                JOptionPane.showMessageDialog(this,
                        "Switched to Database Storage mode.",
                        "Mode Changed", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                // If database connection fails show an error
                JOptionPane.showMessageDialog(this,
                        "Could not connect to database: " + ex.getMessage() +
                        "\nPlease check your MySQL settings in DatabaseConnection.java",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        settingsMenu.add(fileModeItem);
        settingsMenu.add(dbModeItem);
        menuBar.add(settingsMenu);

        // "Help" menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Chuka University\nStudent Records Management System\n" +
                    "COSC 223 - Introduction to Java Programming\n" +
                    "Version 1.0",
                    "About", JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }


    private void createMainContent() {
        tabbedPane = new JTabbedPane();

        // Add each management panel as a tab
        tabbedPane.addTab("Students",            new StudentPanel(studentService, progressionService));
        tabbedPane.addTab("Courses",             new CoursePanel(courseService));
        tabbedPane.addTab("Tutors",              new TutorPanel(tutorService));
        tabbedPane.addTab("Enrollments",         new EnrollmentPanel(enrollmentService, studentService, courseService));
        tabbedPane.addTab("Exam Results",        new ExaminationResultPanel(resultService, enrollmentService));
        tabbedPane.addTab("Course Allocation",   new CourseAllocationPanel(allocationService, courseService, tutorService));
        tabbedPane.addTab("Reports",             new ReportPanel(reportService));

        // Add the tabbed pane to the center of the window
        add(tabbedPane, BorderLayout.CENTER);
    }


    private void refreshTabs() {
        tabbedPane.removeAll(); // Remove all existing tabs

        // Add them back with the updated services
        tabbedPane.addTab("Students",            new StudentPanel(studentService, progressionService));
        tabbedPane.addTab("Courses",             new CoursePanel(courseService));
        tabbedPane.addTab("Tutors",              new TutorPanel(tutorService));
        tabbedPane.addTab("Enrollments",         new EnrollmentPanel(enrollmentService, studentService, courseService));
        tabbedPane.addTab("Exam Results",        new ExaminationResultPanel(resultService, enrollmentService));
        tabbedPane.addTab("Course Allocation",   new CourseAllocationPanel(allocationService, courseService, tutorService));
        tabbedPane.addTab("Reports",             new ReportPanel(reportService));

        tabbedPane.revalidate(); // Refresh the display
        tabbedPane.repaint();
    }


    private void createStatusBar() {
        statusLabel = new JLabel("Mode: File Storage");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        add(statusLabel, BorderLayout.SOUTH);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
