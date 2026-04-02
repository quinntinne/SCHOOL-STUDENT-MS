

import java.util.List;
import java.util.Optional;

public class TutorService {

    private TutorDAO tutorDAO;

    public TutorService(TutorDAO tutorDAO) {
        this.tutorDAO = tutorDAO;
    }


  
    public void addTutor(Tutor tutor) throws Exception {
        // Check tutorId is not empty
        if (tutor.getTutorId() == null || tutor.getTutorId().trim().isEmpty()) {
            throw new Exception("Tutor ID cannot be empty.");
        }

        // Check names are not empty
        if (tutor.getFirstName() == null || tutor.getFirstName().trim().isEmpty()) {
            throw new Exception("First name cannot be empty.");
        }

        if (tutor.getLastName() == null || tutor.getLastName().trim().isEmpty()) {
            throw new Exception("Last name cannot be empty.");
        }

        // Check no duplicate tutorId exists
        Optional<Tutor> existing = tutorDAO.getTutorById(tutor.getTutorId());
        if (existing.isPresent()) {
            throw new Exception("A tutor with ID " + tutor.getTutorId() +
                                " already exists.");
        }

        tutorDAO.addTutor(tutor);
    }

    public void updateTutor(Tutor tutor) throws Exception {
        Optional<Tutor> existing = tutorDAO.getTutorById(tutor.getTutorId());
        if (!existing.isPresent()) {
            throw new Exception("Tutor with ID " + tutor.getTutorId() + " not found.");
        }

        if (tutor.getFirstName() == null || tutor.getFirstName().trim().isEmpty()) {
            throw new Exception("First name cannot be empty.");
        }

        if (tutor.getLastName() == null || tutor.getLastName().trim().isEmpty()) {
            throw new Exception("Last name cannot be empty.");
        }

        tutorDAO.updateTutor(tutor);
    }


    public void deleteTutor(String tutorId) throws Exception {
        Optional<Tutor> existing = tutorDAO.getTutorById(tutorId);
        if (!existing.isPresent()) {
            throw new Exception("Tutor with ID " + tutorId + " not found.");
        }

        tutorDAO.deleteTutor(tutorId);
    }


    public Tutor getTutorById(String tutorId) throws Exception {
        Optional<Tutor> tutor = tutorDAO.getTutorById(tutorId);
        if (!tutor.isPresent()) {
            throw new Exception("Tutor with ID " + tutorId + " not found.");
        }
        return tutor.get();
    }


    public List<Tutor> getAllTutors() throws Exception {
        return tutorDAO.getAllTutors();
    }


    public List<Tutor> searchTutors(String keyword) throws Exception {
        List<Tutor> allTutors = tutorDAO.getAllTutors();
        List<Tutor> matchingTutors = new java.util.ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Tutor tutor : allTutors) {
            if (tutor.getTutorId().toLowerCase().contains(lowerKeyword)
                    || tutor.getFirstName().toLowerCase().contains(lowerKeyword)
                    || tutor.getLastName().toLowerCase().contains(lowerKeyword)
                    || tutor.getDepartment().toLowerCase().contains(lowerKeyword)) {
                matchingTutors.add(tutor);
            }
        }

        return matchingTutors;
    }
}