

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentDatabaseDAO implements EnrollmentDAO {


    @Override
    public void addEnrollment(Enrollment enrollment) throws Exception {
        String sql = "INSERT INTO enrollments (enrollment_id, student_id, course_id, " +
                     "semester, year, enrollment_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, enrollment.getEnrollmentId());
            pstmt.setString(2, enrollment.getStudentId());
            pstmt.setString(3, enrollment.getCourseId());
            pstmt.setInt(4, enrollment.getSemester());
            pstmt.setInt(5, enrollment.getYear());
            pstmt.setDate(6, Date.valueOf(enrollment.getEnrollmentDate()));

            pstmt.executeUpdate();
        }
    }


    @Override
    public void updateEnrollment(Enrollment enrollment) throws Exception {
        String sql = "UPDATE enrollments SET student_id=?, course_id=?, " +
                     "semester=?, year=?, enrollment_date=? WHERE enrollment_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, enrollment.getStudentId());
            pstmt.setString(2, enrollment.getCourseId());
            pstmt.setInt(3, enrollment.getSemester());
            pstmt.setInt(4, enrollment.getYear());
            pstmt.setDate(5, Date.valueOf(enrollment.getEnrollmentDate()));
            pstmt.setString(6, enrollment.getEnrollmentId());

            pstmt.executeUpdate();
        }
    }


    @Override
    public void deleteEnrollment(String enrollmentId) throws Exception {
        String sql = "DELETE FROM enrollments WHERE enrollment_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, enrollmentId);
            pstmt.executeUpdate();
        }
    }


    @Override
    public Optional<Enrollment> getEnrollmentById(String enrollmentId) throws Exception {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToEnrollment(rs));
            }
        }

        return Optional.empty();
    }


    @Override
    public List<Enrollment> getAllEnrollments() throws Exception {
        String sql = "SELECT * FROM enrollments";
        List<Enrollment> enrollments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                enrollments.add(mapRowToEnrollment(rs));
            }
        }

        return enrollments;
    }


    @Override
    public List<Enrollment> getEnrollmentsByStudent(String studentId) throws Exception {
        String sql = "SELECT * FROM enrollments WHERE student_id=?";
        List<Enrollment> enrollments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                enrollments.add(mapRowToEnrollment(rs));
            }
        }

        return enrollments;
    }


    @Override
    public List<Enrollment> getEnrollmentsByCourse(String courseId, int semester, int year)
            throws Exception {
        String sql = "SELECT * FROM enrollments WHERE course_id=? " +
                     "AND semester=? AND year=?";
        List<Enrollment> enrollments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            pstmt.setInt(2, semester);
            pstmt.setInt(3, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                enrollments.add(mapRowToEnrollment(rs));
            }
        }

        return enrollments;
    }


    @Override
    public int countEnrollmentsByStudent(String studentId, int semester, int year)
            throws Exception {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id=? " +
                     "AND semester=? AND year=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setInt(2, semester);
            pstmt.setInt(3, year);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1); // Return the count
            }
        }

        return 0;
    }


    private Enrollment mapRowToEnrollment(ResultSet rs) throws SQLException {
        return new Enrollment(
            rs.getString("enrollment_id"),
            rs.getString("student_id"),
            rs.getString("course_id"),
            rs.getInt("semester"),
            rs.getInt("year"),
            rs.getDate("enrollment_date").toLocalDate()
        );
    }
}