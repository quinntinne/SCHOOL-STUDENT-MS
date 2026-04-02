

import java.util.List;
import java.util.Optional;

public interface TutorDAO {

    // ADD a new tutor
    void addTutor(Tutor tutor) throws Exception;

    // UPDATE an existing tutor
    void updateTutor(Tutor tutor) throws Exception;

    // DELETE a tutor by their tutorId
    void deleteTutor(String tutorId) throws Exception;

    // FIND one tutor by their tutorId
    Optional<Tutor> getTutorById(String tutorId) throws Exception;

    // GET ALL tutors
    List<Tutor> getAllTutors() throws Exception;
}