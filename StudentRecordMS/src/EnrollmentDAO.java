

import java.util.List;
import java.util.Optional;

public interface EnrollmentDAO {

    // ADD a new enrollment
    void addEnrollment(Enrollment enrollment) throws Exception;

    // UPDATE an existing enrollment
    void updateEnrollment(Enrollment enrollment) throws Exception;

    // DELETE an enrollment by its enrollmentId
    void deleteEnrollment(String enrollmentId) throws Exception;

    // FIND one enrollment by its enrollmentId
    Optional<Enrollment> getEnrollmentById(String enrollmentId) throws Exception;

    // GET ALL enrollments
    List<Enrollment> getAllEnrollments() throws Exception;

    // GET ALL enrollments for a specific student
    // Used to see all courses a student is registered for
    List<Enrollment> getEnrollmentsByStudent(String studentId) throws Exception;


    // Used for the Course Enrollment Report
    List<Enrollment> getEnrollmentsByCourse(String courseId, int semester, int year)
            throws Exception;


    // Used to enforce the minimum 10 courses per semester rule
    int countEnrollmentsByStudent(String studentId, int semester, int year)
            throws Exception;
}