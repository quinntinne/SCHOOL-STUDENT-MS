
import java.util.List;
import java.util.Optional;

public class EnrollmentService {

    private EnrollmentDAO enrollmentDAO;

    public EnrollmentService(EnrollmentDAO enrollmentDAO) {
        this.enrollmentDAO = enrollmentDAO;
    }

 
    public void addEnrollment(Enrollment enrollment) throws Exception {
        // Check enrollmentId is not empty
        if (enrollment.getEnrollmentId() == null ||
                enrollment.getEnrollmentId().trim().isEmpty()) {
            throw new Exception("Enrollment ID cannot be empty.");
        }

        // Check studentId is not empty
        if (enrollment.getStudentId() == null ||
                enrollment.getStudentId().trim().isEmpty()) {
            throw new Exception("Student ID cannot be empty.");
        }

        // Check courseId is not empty
        if (enrollment.getCourseId() == null ||
                enrollment.getCourseId().trim().isEmpty()) {
            throw new Exception("Course ID cannot be empty.");
        }

        // Check no duplicate enrollmentId exists
        Optional<Enrollment> existing = enrollmentDAO
                .getEnrollmentById(enrollment.getEnrollmentId());
        if (existing.isPresent()) {
            throw new Exception("An enrollment with ID " +
                    enrollment.getEnrollmentId() + " already exists.");
        }

        // Check the student is not already enrolled in this course
        // in the same semester and year
        List<Enrollment> studentEnrollments = enrollmentDAO
                .getEnrollmentsByStudent(enrollment.getStudentId());

        for (Enrollment e : studentEnrollments) {
            if (e.getCourseId().equals(enrollment.getCourseId())
                    && e.getSemester() == enrollment.getSemester()
                    && e.getYear() == enrollment.getYear()) {
                throw new Exception("Student is already enrolled in course " +
                        enrollment.getCourseId() + " for this semester.");
            }
        }

        // All checks passed — save the enrollment
        enrollmentDAO.addEnrollment(enrollment);
    }


    public void updateEnrollment(Enrollment enrollment) throws Exception {
        Optional<Enrollment> existing = enrollmentDAO
                .getEnrollmentById(enrollment.getEnrollmentId());
        if (!existing.isPresent()) {
            throw new Exception("Enrollment with ID " +
                    enrollment.getEnrollmentId() + " not found.");
        }

        enrollmentDAO.updateEnrollment(enrollment);
    }


    public void deleteEnrollment(String enrollmentId) throws Exception {
        Optional<Enrollment> existing = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (!existing.isPresent()) {
            throw new Exception("Enrollment with ID " + enrollmentId + " not found.");
        }

        enrollmentDAO.deleteEnrollment(enrollmentId);
    }


    public Enrollment getEnrollmentById(String enrollmentId) throws Exception {
        Optional<Enrollment> enrollment = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (!enrollment.isPresent()) {
            throw new Exception("Enrollment with ID " + enrollmentId + " not found.");
        }
        return enrollment.get();
    }


    public List<Enrollment> getAllEnrollments() throws Exception {
        return enrollmentDAO.getAllEnrollments();
    }


    public List<Enrollment> getEnrollmentsByStudent(String studentId) throws Exception {
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }


    public List<Enrollment> getEnrollmentsByCourse(String courseId,
            int semester, int year) throws Exception {
        return enrollmentDAO.getEnrollmentsByCourse(courseId, semester, year);
    }


    public boolean hasMinimumCourseLoad(String studentId, int semester, int year)
            throws Exception {
        int count = enrollmentDAO.countEnrollmentsByStudent(studentId, semester, year);
        return count >= 10; // Minimum 10 courses required per semester
    }


    public List<Enrollment> searchEnrollments(String keyword) throws Exception {
        List<Enrollment> allEnrollments = enrollmentDAO.getAllEnrollments();
        List<Enrollment> matchingEnrollments = new java.util.ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Enrollment enrollment : allEnrollments) {
            if (enrollment.getEnrollmentId().toLowerCase().contains(lowerKeyword)
                    || enrollment.getStudentId().toLowerCase().contains(lowerKeyword)
                    || enrollment.getCourseId().toLowerCase().contains(lowerKeyword)) {
                matchingEnrollments.add(enrollment);
            }
        }

        return matchingEnrollments;
    }
}