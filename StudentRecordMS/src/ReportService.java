

import java.util.ArrayList;
import java.util.List;

public class ReportService {

    // We need access to multiple DAOs to gather data for reports
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private TutorDAO tutorDAO;
    private EnrollmentDAO enrollmentDAO;
    private ExaminationResultDAO resultDAO;
    private CourseAllocationDAO allocationDAO;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------
    public ReportService(StudentDAO studentDAO, CourseDAO courseDAO,
                         TutorDAO tutorDAO, EnrollmentDAO enrollmentDAO,
                         ExaminationResultDAO resultDAO,
                         CourseAllocationDAO allocationDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.tutorDAO = tutorDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.resultDAO = resultDAO;
        this.allocationDAO = allocationDAO;
    }


    public String generateStudentTranscript(String studentId) throws Exception {

        // Get student details
        java.util.Optional<Student> studentOpt = studentDAO.getStudentById(studentId);
        if (!studentOpt.isPresent()) {
            throw new Exception("Student with ID " + studentId + " not found.");
        }
        Student student = studentOpt.get();

        // Build the transcript text using StringBuilder
        StringBuilder transcript = new StringBuilder();
        transcript.append("========================================\n");
        transcript.append("       CHUKA UNIVERSITY TRANSCRIPT      \n");
        transcript.append("========================================\n");
        transcript.append("Student ID   : ").append(student.getStudentId()).append("\n");
        transcript.append("Name         : ").append(student.getFirstName())
                  .append(" ").append(student.getLastName()).append("\n");
        transcript.append("Major        : ").append(student.getMajor()).append("\n");
        transcript.append("Current Year : ").append(student.getCurrentYear()).append("\n");
        transcript.append("Semester     : ").append(student.getCurrentSemester()).append("\n");
        transcript.append("Status       : ").append(student.getAcademicStatus()).append("\n");
        transcript.append("----------------------------------------\n");
        transcript.append("ACADEMIC RESULTS:\n");
        transcript.append(String.format("%-15s %-30s %-8s %-5s\n",
                "Enrollment ID", "Course", "Score", "Grade"));
        transcript.append("----------------------------------------\n");

        // Get all enrollments for this student
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);

        int totalScore = 0;
        int resultCount = 0;

        for (Enrollment enrollment : enrollments) {
            // Get the course name for this enrollment
            java.util.Optional<Course> courseOpt =
                    courseDAO.getCourseById(enrollment.getCourseId());
            String courseName = courseOpt.isPresent() ?
                    courseOpt.get().getCourseName() : "Unknown Course";

            // Get the result for this enrollment
            List<ExaminationResult> results =
                    resultDAO.getResultsByEnrollment(enrollment.getEnrollmentId());

            if (!results.isEmpty()) {
                ExaminationResult result = results.get(0);
                transcript.append(String.format("%-15s %-30s %-8d %-5s\n",
                        enrollment.getEnrollmentId(),
                        courseName,
                        result.getScore(),
                        result.getGrade()));
                totalScore += result.getScore();
                resultCount++;
            } else {
                // No result recorded yet for this enrollment
                transcript.append(String.format("%-15s %-30s %-8s %-5s\n",
                        enrollment.getEnrollmentId(),
                        courseName,
                        "N/A",
                        "N/A"));
            }
        }

        // Calculate and display average score
        transcript.append("----------------------------------------\n");
        if (resultCount > 0) {
            double average = (double) totalScore / resultCount;
            transcript.append(String.format("Average Score: %.2f\n", average));
        } else {
            transcript.append("No results recorded yet.\n");
        }
        transcript.append("========================================\n");

        return transcript.toString();
    }

    public String generateCourseEnrollmentReport(String courseId,
            int semester, int year) throws Exception {

        // Get course details
        java.util.Optional<Course> courseOpt = courseDAO.getCourseById(courseId);
        if (!courseOpt.isPresent()) {
            throw new Exception("Course with ID " + courseId + " not found.");
        }
        Course course = courseOpt.get();

        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("      COURSE ENROLLMENT REPORT          \n");
        report.append("========================================\n");
        report.append("Course ID   : ").append(course.getCourseId()).append("\n");
        report.append("Course Name : ").append(course.getCourseName()).append("\n");
        report.append("Semester    : ").append(semester).append("\n");
        report.append("Year        : ").append(year).append("\n");
        report.append("----------------------------------------\n");
        report.append(String.format("%-25s %-20s %-15s\n",
                "Student ID", "Student Name", "Enrollment Date"));
        report.append("----------------------------------------\n");

        // Get all enrollments for this course in the given semester and year
        List<Enrollment> enrollments =
                enrollmentDAO.getEnrollmentsByCourse(courseId, semester, year);

        if (enrollments.isEmpty()) {
            report.append("No students enrolled in this course.\n");
        } else {
            for (Enrollment enrollment : enrollments) {
                // Get the student name for each enrollment
                java.util.Optional<Student> studentOpt =
                        studentDAO.getStudentById(enrollment.getStudentId());
                String studentName = studentOpt.isPresent() ?
                        studentOpt.get().getFirstName() + " " +
                        studentOpt.get().getLastName() : "Unknown";

                report.append(String.format("%-25s %-20s %-15s\n",
                        enrollment.getStudentId(),
                        studentName,
                        enrollment.getEnrollmentDate().toString()));
            }
        }

        report.append("----------------------------------------\n");
        report.append("Total Enrolled: ").append(enrollments.size()).append("\n");
        report.append("========================================\n");

        return report.toString();
    }


    public String generateTutorCourseLoadReport(String tutorId,
            int semester, int year) throws Exception {

        // Get tutor details
        java.util.Optional<Tutor> tutorOpt = tutorDAO.getTutorById(tutorId);
        if (!tutorOpt.isPresent()) {
            throw new Exception("Tutor with ID " + tutorId + " not found.");
        }
        Tutor tutor = tutorOpt.get();

        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("      TUTOR COURSE LOAD REPORT          \n");
        report.append("========================================\n");
        report.append("Tutor ID    : ").append(tutor.getTutorId()).append("\n");
        report.append("Tutor Name  : ").append(tutor.getFirstName())
              .append(" ").append(tutor.getLastName()).append("\n");
        report.append("Department  : ").append(tutor.getDepartment()).append("\n");
        report.append("Semester    : ").append(semester).append("\n");
        report.append("Year        : ").append(year).append("\n");
        report.append("----------------------------------------\n");
        report.append(String.format("%-15s %-30s %-10s\n",
                "Course ID", "Course Name", "Credits"));
        report.append("----------------------------------------\n");

        // Get all allocations for this tutor in the given semester and year
        List<CourseAllocation> allocations =
                allocationDAO.getAllocationsByTutor(tutorId, semester, year);

        if (allocations.isEmpty()) {
            report.append("No courses allocated to this tutor.\n");
        } else {
            int totalCredits = 0;
            for (CourseAllocation allocation : allocations) {
                // Get course details for each allocation
                java.util.Optional<Course> courseOpt =
                        courseDAO.getCourseById(allocation.getCourseId());
                if (courseOpt.isPresent()) {
                    Course course = courseOpt.get();
                    report.append(String.format("%-15s %-30s %-10d\n",
                            course.getCourseId(),
                            course.getCourseName(),
                            course.getCredits()));
                    totalCredits += course.getCredits();
                }
            }
            report.append("----------------------------------------\n");
            report.append("Total Courses : ").append(allocations.size()).append("\n");
            report.append("Total Credits : ").append(totalCredits).append("\n");
        }

        report.append("========================================\n");
        return report.toString();
    }


    public String generateGraduationEligibilityReport() throws Exception {
        List<Student> allStudents = studentDAO.getAllStudents();

        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("   GRADUATION ELIGIBILITY REPORT        \n");
        report.append("========================================\n");
        report.append(String.format("%-25s %-20s %-15s\n",
                "Student ID", "Student Name", "Status"));
        report.append("----------------------------------------\n");

        int eligibleCount = 0;

        for (Student student : allStudents) {
            // Check if student has met graduation requirements
            if (student.getCurrentYear() >= 4 &&
                    studentDAO.hasMetGraduationRequirements(student.getStudentId())) {
                report.append(String.format("%-25s %-20s %-15s\n",
                        student.getStudentId(),
                        student.getFirstName() + " " + student.getLastName(),
                        student.getAcademicStatus()));
                eligibleCount++;
            }
        }

        if (eligibleCount == 0) {
            report.append("No students currently eligible for graduation.\n");
        }

        report.append("----------------------------------------\n");
        report.append("Total Eligible: ").append(eligibleCount).append("\n");
        report.append("========================================\n");

        return report.toString();
    }


    public String generateProbationReport() throws Exception {
        List<Student> allStudents = studentDAO.getAllStudents();

        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("      STUDENTS ON PROBATION REPORT      \n");
        report.append("========================================\n");
        report.append(String.format("%-25s %-20s %-10s %-10s\n",
                "Student ID", "Student Name", "Year", "Semester"));
        report.append("----------------------------------------\n");

        int probationCount = 0;

        for (Student student : allStudents) {
            if (student.getAcademicStatus().equals("On Probation")) {
                report.append(String.format("%-25s %-20s %-10d %-10d\n",
                        student.getStudentId(),
                        student.getFirstName() + " " + student.getLastName(),
                        student.getCurrentYear(),
                        student.getCurrentSemester()));
                probationCount++;
            }
        }

        if (probationCount == 0) {
            report.append("No students currently on probation.\n");
        }

        report.append("----------------------------------------\n");
        report.append("Total on Probation: ").append(probationCount).append("\n");
        report.append("========================================\n");

        return report.toString();
    }
}