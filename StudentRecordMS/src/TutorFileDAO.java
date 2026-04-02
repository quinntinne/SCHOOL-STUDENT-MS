

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TutorFileDAO implements TutorDAO {

    private static final String FILE_NAME = "tutors.txt";


    @Override
    public void addTutor(Tutor tutor) throws Exception {
        FileUtil.writeLine(FILE_NAME, tutor.toString(), true);
    }


    @Override
    public void updateTutor(Tutor tutor) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            Tutor existing = Tutor.fromString(line);
            if (existing.getTutorId().equals(tutor.getTutorId())) {
                updatedLines.add(tutor.toString()); // Replace with updated data
            } else {
                updatedLines.add(line); // Keep unchanged
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public void deleteTutor(String tutorId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            Tutor existing = Tutor.fromString(line);
            if (!existing.getTutorId().equals(tutorId)) {
                updatedLines.add(line); // Keep all tutors except the deleted one
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public Optional<Tutor> getTutorById(String tutorId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);

        for (String line : lines) {
            Tutor tutor = Tutor.fromString(line);
            if (tutor.getTutorId().equals(tutorId)) {
                return Optional.of(tutor); // Found — return it
            }
        }

        return Optional.empty(); // Not found
    }


    @Override
    public List<Tutor> getAllTutors() throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<Tutor> tutors = new ArrayList<>();

        for (String line : lines) {
            tutors.add(Tutor.fromString(line));
        }

        return tutors;
    }
}