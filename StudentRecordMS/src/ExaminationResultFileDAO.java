

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExaminationResultFileDAO implements ExaminationResultDAO {

    private static final String FILE_NAME = "examination_results.txt";


    @Override
    public void addResult(ExaminationResult result) throws Exception {
        FileUtil.writeLine(FILE_NAME, result.toString(), true);
    }


    @Override
    public void updateResult(ExaminationResult result) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            ExaminationResult existing = ExaminationResult.fromString(line);
            if (existing.getResultId().equals(result.getResultId())) {
                updatedLines.add(result.toString()); // Replace with updated data
            } else {
                updatedLines.add(line); // Keep unchanged
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public void deleteResult(String resultId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            ExaminationResult existing = ExaminationResult.fromString(line);
            if (!existing.getResultId().equals(resultId)) {
                updatedLines.add(line); // Keep all except the deleted one
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public Optional<ExaminationResult> getResultById(String resultId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);

        for (String line : lines) {
            ExaminationResult result = ExaminationResult.fromString(line);
            if (result.getResultId().equals(resultId)) {
                return Optional.of(result); // Found — return it
            }
        }

        return Optional.empty(); // Not found
    }


    @Override
    public List<ExaminationResult> getAllResults() throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<ExaminationResult> results = new ArrayList<>();

        for (String line : lines) {
            results.add(ExaminationResult.fromString(line));
        }

        return results;
    }


    @Override
    public List<ExaminationResult> getResultsByEnrollment(String enrollmentId)
            throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<ExaminationResult> results = new ArrayList<>();

        for (String line : lines) {
            ExaminationResult result = ExaminationResult.fromString(line);
            if (result.getEnrollmentId().equals(enrollmentId)) {
                results.add(result); // Only add if it belongs to this enrollment
            }
        }

        return results;
    }


    @Override
    public List<ExaminationResult> getResultsByStudent(String studentId)
            throws Exception {
        // First get all enrollments for this student
        List<String> enrollmentLines = FileUtil.readAllLines("enrollments.txt");
        List<String> resultLines = FileUtil.readAllLines(FILE_NAME);
        List<ExaminationResult> results = new ArrayList<>();

        // Build a list of enrollmentIds that belong to this student
        List<String> studentEnrollmentIds = new ArrayList<>();
        for (String eLine : enrollmentLines) {
            Enrollment enrollment = Enrollment.fromString(eLine);
            if (enrollment.getStudentId().equals(studentId)) {
                studentEnrollmentIds.add(enrollment.getEnrollmentId());
            }
        }

        // Now find all results that match those enrollmentIds
        for (String rLine : resultLines) {
            ExaminationResult result = ExaminationResult.fromString(rLine);
            if (studentEnrollmentIds.contains(result.getEnrollmentId())) {
                results.add(result);
            }
        }

        return results;
    }
}