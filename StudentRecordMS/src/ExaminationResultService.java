
import java.util.List;
import java.util.Optional;

public class ExaminationResultService {

    private ExaminationResultDAO resultDAO;

    public ExaminationResultService(ExaminationResultDAO resultDAO) {
        this.resultDAO = resultDAO;
    }


    public void addResult(ExaminationResult result) throws Exception {
        // Check resultId is not empty
        if (result.getResultId() == null || result.getResultId().trim().isEmpty()) {
            throw new Exception("Result ID cannot be empty.");
        }

        // Check enrollmentId is not empty
        if (result.getEnrollmentId() == null ||
                result.getEnrollmentId().trim().isEmpty()) {
            throw new Exception("Enrollment ID cannot be empty.");
        }

        // Check score is between 0 and 100
        if (result.getScore() < 0 || result.getScore() > 100) {
            throw new Exception("Score must be between 0 and 100.");
        }

        // Check no duplicate resultId exists
        Optional<ExaminationResult> existing = resultDAO
                .getResultById(result.getResultId());
        if (existing.isPresent()) {
            throw new Exception("A result with ID " + result.getResultId() +
                                " already exists.");
        }

        // Automatically calculate and set the grade based on the score
        // This ensures grade is always consistent with the score
        String calculatedGrade = ExaminationResult.calculateGrade(result.getScore());
        result.setGrade(calculatedGrade);

        resultDAO.addResult(result);
    }


    public void updateResult(ExaminationResult result) throws Exception {
        Optional<ExaminationResult> existing = resultDAO
                .getResultById(result.getResultId());
        if (!existing.isPresent()) {
            throw new Exception("Result with ID " + result.getResultId() + " not found.");
        }

        if (result.getScore() < 0 || result.getScore() > 100) {
            throw new Exception("Score must be between 0 and 100.");
        }

        // Recalculate grade in case score was changed
        result.setGrade(ExaminationResult.calculateGrade(result.getScore()));

        resultDAO.updateResult(result);
    }


    public void deleteResult(String resultId) throws Exception {
        Optional<ExaminationResult> existing = resultDAO.getResultById(resultId);
        if (!existing.isPresent()) {
            throw new Exception("Result with ID " + resultId + " not found.");
        }

        resultDAO.deleteResult(resultId);
    }

    public ExaminationResult getResultById(String resultId) throws Exception {
        Optional<ExaminationResult> result = resultDAO.getResultById(resultId);
        if (!result.isPresent()) {
            throw new Exception("Result with ID " + resultId + " not found.");
        }
        return result.get();
    }


    public List<ExaminationResult> getAllResults() throws Exception {
        return resultDAO.getAllResults();
    }


    public List<ExaminationResult> getResultsByStudent(String studentId)
            throws Exception {
        return resultDAO.getResultsByStudent(studentId);
    }


    public List<ExaminationResult> searchResults(String keyword) throws Exception {
        List<ExaminationResult> allResults = resultDAO.getAllResults();
        List<ExaminationResult> matchingResults = new java.util.ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (ExaminationResult result : allResults) {
            if (result.getResultId().toLowerCase().contains(lowerKeyword)
                    || result.getEnrollmentId().toLowerCase().contains(lowerKeyword)) {
                matchingResults.add(result);
            }
        }

        return matchingResults;
    }
}