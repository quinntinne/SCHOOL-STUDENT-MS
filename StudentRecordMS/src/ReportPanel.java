

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReportPanel extends JPanel {

    // Service this panel uses
    private ReportService reportService;

    // -------------------------------------------------------------------------
    // INPUT FIELDS
    // -------------------------------------------------------------------------
    private JTextField txtStudentId;     // For student transcript
    private JTextField txtCourseId;      // For course enrollment report
    private JTextField txtTutorId;       // For tutor course load report
    private JTextField txtSemester;      // Shared semester field
    private JTextField txtYear;          // Shared year field

    private JTextArea reportOutput;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------
    public ReportPanel(ReportService reportService) {
        this.reportService = reportService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createControlPanel(), BorderLayout.NORTH);
        add(createOutputPanel(), BorderLayout.CENTER);
    }


    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Generate Reports"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ---- SECTION 1: Student Academic Transcript ----
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 4;
        JLabel lblSection1 = new JLabel("── Student Reports ──────────────────");
        lblSection1.setForeground(new Color(70, 130, 180));
        lblSection1.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(lblSection1, gbc);
        gbc.gridwidth = 1;

        // Student ID input and button
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        txtStudentId = new JTextField(15);
        controlPanel.add(txtStudentId, gbc);

        gbc.gridx = 2;
        JButton btnTranscript = new JButton("Generate Transcript");
        btnTranscript.setBackground(new Color(70, 130, 180));
        btnTranscript.setForeground(Color.WHITE);
        btnTranscript.addActionListener(e -> generateTranscript());
        controlPanel.add(btnTranscript, gbc);

        // ---- SECTION 2: Course Enrollment Report ----
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        JLabel lblSection2 = new JLabel("── Course Reports ───────────────────");
        lblSection2.setForeground(new Color(46, 139, 87));
        lblSection2.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(lblSection2, gbc);
        gbc.gridwidth = 1;

        // Course ID, Semester, Year inputs and button
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(new JLabel("Course ID:"), gbc);
        gbc.gridx = 1;
        txtCourseId = new JTextField(15);
        controlPanel.add(txtCourseId, gbc);

        gbc.gridx = 2;
        controlPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 3;
        txtSemester = new JTextField(10);
        controlPanel.add(txtSemester, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        controlPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        txtYear = new JTextField(15);
        controlPanel.add(txtYear, gbc);

        gbc.gridx = 2;
        JButton btnCourseReport = new JButton("Course Enrollment Report");
        btnCourseReport.setBackground(new Color(46, 139, 87));
        btnCourseReport.setForeground(Color.WHITE);
        btnCourseReport.addActionListener(e -> generateCourseReport());
        controlPanel.add(btnCourseReport, gbc);

        // ---- SECTION 3: Tutor Course Load Report ----
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 4;
        JLabel lblSection3 = new JLabel("── Tutor Reports ────────────────────");
        lblSection3.setForeground(new Color(128, 0, 128));
        lblSection3.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(lblSection3, gbc);
        gbc.gridwidth = 1;

        // Tutor ID input and button
        // Note: uses same semester and year fields from above
        gbc.gridx = 0; gbc.gridy = 6;
        controlPanel.add(new JLabel("Tutor ID:"), gbc);
        gbc.gridx = 1;
        txtTutorId = new JTextField(15);
        controlPanel.add(txtTutorId, gbc);

        gbc.gridx = 2;
        JButton btnTutorReport = new JButton("Tutor Course Load Report");
        btnTutorReport.setBackground(new Color(128, 0, 128));
        btnTutorReport.setForeground(Color.WHITE);
        btnTutorReport.addActionListener(e -> generateTutorReport());
        controlPanel.add(btnTutorReport, gbc);

        // ---- SECTION 4: Graduation and Probation Reports ----
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 4;
        JLabel lblSection4 = new JLabel("── Academic Status Reports ──────────");
        lblSection4.setForeground(new Color(178, 34, 34));
        lblSection4.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(lblSection4, gbc);
        gbc.gridwidth = 1;

        // Graduation eligibility button
        gbc.gridx = 0; gbc.gridy = 8;
        JButton btnGraduationReport = new JButton("Graduation Eligibility Report");
        btnGraduationReport.setBackground(new Color(255, 140, 0));
        btnGraduationReport.setForeground(Color.WHITE);
        btnGraduationReport.addActionListener(e -> generateGraduationReport());
        gbc.gridwidth = 2;
        controlPanel.add(btnGraduationReport, gbc);
        gbc.gridwidth = 1;

        // Probation report button
        gbc.gridx = 2; gbc.gridy = 8;
        JButton btnProbationReport = new JButton("Students on Probation Report");
        btnProbationReport.setBackground(new Color(178, 34, 34));
        btnProbationReport.setForeground(Color.WHITE);
        btnProbationReport.addActionListener(e -> generateProbationReport());
        gbc.gridwidth = 2;
        controlPanel.add(btnProbationReport, gbc);
        gbc.gridwidth = 1;

        // Clear output button
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 4;
        JButton btnClear = new JButton("Clear Report Output");
        btnClear.addActionListener(e -> reportOutput.setText(""));
        controlPanel.add(btnClear, gbc);
        gbc.gridwidth = 1;

        return controlPanel;
    }


    private JPanel createOutputPanel() {
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Report Output"));

        // Large text area — read only, monospaced font for clean alignment
        reportOutput = new JTextArea();
        reportOutput.setEditable(false);
        reportOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportOutput.setBackground(new Color(250, 250, 250));
        reportOutput.setText("Select a report type above and click the " +
                             "corresponding button to generate a report.");

        // Put it in a scroll pane so long reports can be scrolled
        JScrollPane scrollPane = new JScrollPane(reportOutput);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        return outputPanel;
    }

    private void generateTranscript() {
        String studentId = txtStudentId.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a Student ID.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String report = reportService.generateStudentTranscript(studentId);
            reportOutput.setText(report);
            // Scroll to the top of the report
            reportOutput.setCaretPosition(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating transcript: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateCourseReport() {
        String courseId  = txtCourseId.getText().trim();
        String semText   = txtSemester.getText().trim();
        String yearText  = txtYear.getText().trim();

        if (courseId.isEmpty() || semText.isEmpty() || yearText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter Course ID, Semester and Year.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int semester = Integer.parseInt(semText);
            int year     = Integer.parseInt(yearText);

            String report = reportService.generateCourseEnrollmentReport(
                    courseId, semester, year);
            reportOutput.setText(report);
            reportOutput.setCaretPosition(0);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Semester and Year must be numbers.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void generateTutorReport() {
        String tutorId  = txtTutorId.getText().trim();
        String semText  = txtSemester.getText().trim();
        String yearText = txtYear.getText().trim();

        if (tutorId.isEmpty() || semText.isEmpty() || yearText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter Tutor ID, Semester and Year.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int semester = Integer.parseInt(semText);
            int year     = Integer.parseInt(yearText);

            String report = reportService.generateTutorCourseLoadReport(
                    tutorId, semester, year);
            reportOutput.setText(report);
            reportOutput.setCaretPosition(0);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Semester and Year must be numbers.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void generateGraduationReport() {
        try {
            String report = reportService.generateGraduationEligibilityReport();
            reportOutput.setText(report);
            reportOutput.setCaretPosition(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating graduation report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

 
    private void generateProbationReport() {
        try {
            String report = reportService.generateProbationReport();
            reportOutput.setText(report);
            reportOutput.setCaretPosition(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating probation report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}