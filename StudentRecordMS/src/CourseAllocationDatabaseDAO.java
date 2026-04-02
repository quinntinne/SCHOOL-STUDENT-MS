// This file handles saving and loading CourseAllocation data using MYSQL DATABASE.

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseAllocationDatabaseDAO implements CourseAllocationDAO {


    @Override
    public void addAllocation(CourseAllocation allocation) throws Exception {
        String sql = "INSERT INTO course_allocations (allocation_id, course_id, " +
                     "tutor_id, semester, year) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, allocation.getAllocationId());
            pstmt.setString(2, allocation.getCourseId());
            pstmt.setString(3, allocation.getTutorId());
            pstmt.setInt(4, allocation.getSemester());
            pstmt.setInt(5, allocation.getYear());

            pstmt.executeUpdate();
        }
    }


    @Override
    public void updateAllocation(CourseAllocation allocation) throws Exception {
        String sql = "UPDATE course_allocations SET course_id=?, tutor_id=?, " +
                     "semester=?, year=? WHERE allocation_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, allocation.getCourseId());
            pstmt.setString(2, allocation.getTutorId());
            pstmt.setInt(3, allocation.getSemester());
            pstmt.setInt(4, allocation.getYear());
            pstmt.setString(5, allocation.getAllocationId());

            pstmt.executeUpdate();
        }
    }


    @Override
    public void deleteAllocation(String allocationId) throws Exception {
        String sql = "DELETE FROM course_allocations WHERE allocation_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, allocationId);
            pstmt.executeUpdate();
        }
    }


    @Override
    public Optional<CourseAllocation> getAllocationById(String allocationId)
            throws Exception {
        String sql = "SELECT * FROM course_allocations WHERE allocation_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, allocationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToAllocation(rs));
            }
        }

        return Optional.empty();
    }


    @Override
    public List<CourseAllocation> getAllAllocations() throws Exception {
        String sql = "SELECT * FROM course_allocations";
        List<CourseAllocation> allocations = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                allocations.add(mapRowToAllocation(rs));
            }
        }

        return allocations;
    }


    @Override
    public List<CourseAllocation> getAllocationsByTutor(String tutorId,
            int semester, int year) throws Exception {
        String sql = "SELECT * FROM course_allocations WHERE tutor_id=? " +
                     "AND semester=? AND year=?";
        List<CourseAllocation> allocations = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tutorId);
            pstmt.setInt(2, semester);
            pstmt.setInt(3, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allocations.add(mapRowToAllocation(rs));
            }
        }

        return allocations;
    }


    @Override
    public List<CourseAllocation> getAllocationsByCourse(String courseId,
            int semester, int year) throws Exception {
        String sql = "SELECT * FROM course_allocations WHERE course_id=? " +
                     "AND semester=? AND year=?";
        List<CourseAllocation> allocations = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            pstmt.setInt(2, semester);
            pstmt.setInt(3, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allocations.add(mapRowToAllocation(rs));
            }
        }

        return allocations;
    }


    private CourseAllocation mapRowToAllocation(ResultSet rs) throws SQLException {
        return new CourseAllocation(
            rs.getString("allocation_id"),
            rs.getString("course_id"),
            rs.getString("tutor_id"),
            rs.getInt("semester"),
            rs.getInt("year")
        );
    }
}