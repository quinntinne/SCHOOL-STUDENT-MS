
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDatabaseDAO implements StudentDAO {


    @Override
    public void addStudent(Student student) throws Exception {
        // The SQL query — the ? marks are placeholders for actual values
        String sql = "INSERT INTO students (student_id, first_name, last_name, " +
                     "date_of_birth, major, current_semester, current_year, " +
                     "academic_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // try-with-resources automatically closes the connection when done
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Fill in the ? placeholders with actual values
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setDate(4, Date.valueOf(student.getDateOfBirth()));
            pstmt.setString(5, student.getMajor());
            pstmt.setInt(6, student.getCurrentSemester());
            pstmt.setInt(7, student.getCurrentYear());
            pstmt.setString(8, student.getAcademicStatus());

            pstmt.executeUpdate(); // Run the INSERT query
        }
    }


    @Override
    public void updateStudent(Student student) throws Exception {
        String sql = "UPDATE students SET first_name=?, last_name=?, " +
                     "date_of_birth=?, major=?, current_semester=?, " +
                     "current_year=?, academic_status=? WHERE student_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setDate(3, Date.valueOf(student.getDateOfBirth()));
            pstmt.setString(4, student.getMajor());
            pstmt.setInt(5, student.getCurrentSemester());
            pstmt.setInt(6, student.getCurrentYear());
            pstmt.setString(7, student.getAcademicStatus());
            pstmt.setString(8, student.getStudentId()); // WHERE clause

            pstmt.executeUpdate(); // Run the UPDATE query
        }
    }


    @Override
    public void deleteStudent(String studentId) throws Exception {
        String sql = "DELETE FROM students WHERE student_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.executeUpdate(); // Run the DELETE query
        }
    }


    @Override
    public Optional<Student> getStudentById(String studentId) throws Exception {
        String sql = "SELECT * FROM students WHERE student_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery(); // Run the SELECT query

            if (rs.next()) { // If a result was found
                return Optional.of(mapRowToStudent(rs)); // Convert and return
            }
        }

        return Optional.empty(); // No student found
    }


    @Override
    public List<Student> getAllStudents() throws Exception {
        String sql = "SELECT * FROM students";
        List<Student> students = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) { // Loop through every row returned
                students.add(mapRowToStudent(rs));
            }
        }

        return students;
    }


    @Override
    public int countPassedCourses(String studentId, int semester, int year)
            throws Exception {

        String sql = "SELECT COUNT(*) FROM enrollments e " +
                     "JOIN examination_results r ON e.enrollment_id = r.enrollment_id " +
                     "WHERE e.student_id=? AND e.semester=? AND e.year=? AND r.score >= 40";

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


    @Override
    public boolean hasMetGraduationRequirements(String studentId) throws Exception {
        // This query sums the credits of all courses the student has passed
        String sql = "SELECT SUM(c.credits) FROM enrollments e " +
                     "JOIN examination_results r ON e.enrollment_id = r.enrollment_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.student_id=? AND r.score >= 40";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int totalCredits = rs.getInt(1);
                return totalCredits >= 120; // 120 credits needed to graduate
            }
        }

        return false;
    }


    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getString("student_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getDate("date_of_birth").toLocalDate(), // Convert SQL Date to LocalDate
            rs.getString("major"),
            rs.getInt("current_semester"),
            rs.getInt("current_year"),
            rs.getString("academic_status")
        );
    }
}