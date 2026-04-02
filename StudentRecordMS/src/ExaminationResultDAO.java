

import java.util.List;
import java.util.Optional;

public interface ExaminationResultDAO {

    // ADD a new examination result
    void addResult(ExaminationResult result) throws Exception;

    // UPDATE an existing result
    void updateResult(ExaminationResult result) throws Exception;

    // DELETE a result by its resultId
    void deleteResult(String resultId) throws Exception;

    // FIND one result by its resultId
    Optional<ExaminationResult> getResultById(String resultId) throws Exception;

    // GET ALL results
    List<ExaminationResult> getAllResults() throws Exception;

    // GET ALL results for a specific enrollment
    // Used to find the score/grade for a particular course a student took
    List<ExaminationResult> getResultsByEnrollment(String enrollmentId) throws Exception;

    // GET ALL results for a specific student
    // Used when generating a student's academic transcript
    List<ExaminationResult> getResultsByStudent(String studentId) throws Exception;
}