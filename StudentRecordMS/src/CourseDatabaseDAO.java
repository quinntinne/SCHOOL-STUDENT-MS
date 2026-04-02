// This file handles saving and loading Course data using MYSQL DATABASE.

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDatabaseDAO implements CourseDAO {


    @Override
    public void addCourse(Course course) throws Exception {
        String sql = "INSERT INTO courses (course_id, course_name, credits, department) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            pstmt.setString(4, course.getDepartment());

            pstmt.executeUpdate();
        }
    }


    @Override
    public void updateCourse(Course course) throws Exception {
        String sql = "UPDATE courses SET course_name=?, credits=?, " +
                     "department=? WHERE course_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseName());
            pstmt.setInt(2, course.getCredits());
            pstmt.setString(3, course.getDepartment());
            pstmt.setString(4, course.getCourseId());

            pstmt.executeUpdate();
        }
    }


    @Override
    public void deleteCourse(String courseId) throws Exception {
        String sql = "DELETE FROM courses WHERE course_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            pstmt.executeUpdate();
        }
    }


    @Override
    public Optional<Course> getCourseById(String courseId) throws Exception {
        String sql = "SELECT * FROM courses WHERE course_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCourse(rs));
            }
        }

        return Optional.empty();
    }


    @Override
    public List<Course> getAllCourses() throws Exception {
        String sql = "SELECT * FROM courses";
        List<Course> courses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
        }

        return courses;
    }


    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        return new Course(
            rs.getString("course_id"),
            rs.getString("course_name"),
            rs.getInt("credits"),
            rs.getString("department")
        );
    }
}