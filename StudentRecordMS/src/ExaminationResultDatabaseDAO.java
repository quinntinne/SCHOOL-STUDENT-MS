// This file handles saving and loading ExaminationResult data using MYSQL DATABASE.

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExaminationResultDatabaseDAO implements ExaminationResultDAO {


    @Override
    public void addResult(ExaminationResult result) throws Exception {
        String sql = "INSERT INTO examination_results (result_id, enrollment_id, " +
                     "score, grade, semester, year) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, result.getResultId());
            pstmt.setString(2, result.getEnrollmentId());
            pstmt.setInt(3, result.getScore());
            pstmt.setString(4, result.getGrade());
            pstmt.setInt(5, result.getSemester());
            pstmt.setInt(6, result.getYear());

            pstmt.executeUpdate();
        }
    }


    @Override
    public void updateResult(ExaminationResult result) throws Exception {
        String sql = "UPDATE examination_results SET enrollment_id=?, score=?, " +
                     "grade=?, semester=?, year=? WHERE result_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, result.getEnrollmentId());
            pstmt.setInt(2, result.getScore());
            pstmt.setString(3, result.getGrade());
            pstmt.setInt(4, result.getSemester());
            pstmt.setInt(5, result.getYear());
            pstmt.setString(6, result.getResultId());

            pstmt.executeUpdate();
        }
    }


    @Override
    public void deleteResult(String resultId) throws Exception {
        String sql = "DELETE FROM examination_results WHERE result_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, resultId);
            pstmt.executeUpdate();
        }
    }


    @Override
    public Optional<ExaminationResult> getResultById(String resultId) throws Exception {
        String sql = "SELECT * FROM examination_results WHERE result_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, resultId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToResult(rs));
            }
        }

        return Optional.empty();
    }


    @Override
    public List<ExaminationResult> getAllResults() throws Exception {
        String sql = "SELECT * FROM examination_results";
        List<ExaminationResult> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapRowToResult(rs));
            }
        }

        return results;
    }


    @Override
    public List<ExaminationResult> getResultsByEnrollment(String enrollmentId)
            throws Exception {
        String sql = "SELECT * FROM examination_results WHERE enrollment_id=?";
        List<ExaminationResult> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(mapRowToResult(rs));
            }
        }

        return results;
    }


    @Override
    public List<ExaminationResult> getResultsByStudent(String studentId)
            throws Exception {
        // JOIN enrollments and examination_results to find results by studentId
        String sql = "SELECT r.* FROM examination_results r " +
                     "JOIN enrollments e ON r.enrollment_id = e.enrollment_id " +
                     "WHERE e.student_id=?";
        List<ExaminationResult> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(mapRowToResult(rs));
            }
        }

        return results;
    }


    private ExaminationResult mapRowToResult(ResultSet rs) throws SQLException {
        return new ExaminationResult(
            rs.getString("result_id"),
            rs.getString("enrollment_id"),
            rs.getInt("score"),
            rs.getString("grade"),
            rs.getInt("semester"),
            rs.getInt("year")
        );
    }
}