

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentFileDAO implements EnrollmentDAO {

    private static final String FILE_NAME = "enrollments.txt";


    @Override
    public void addEnrollment(Enrollment enrollment) throws Exception {
        FileUtil.writeLine(FILE_NAME, enrollment.toString(), true);
    }


    @Override
    public void updateEnrollment(Enrollment enrollment) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            Enrollment existing = Enrollment.fromString(line);
            if (existing.getEnrollmentId().equals(enrollment.getEnrollmentId())) {
                updatedLines.add(enrollment.toString()); // Replace with updated data
            } else {
                updatedLines.add(line); // Keep unchanged
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public void deleteEnrollment(String enrollmentId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            Enrollment existing = Enrollment.fromString(line);
            if (!existing.getEnrollmentId().equals(enrollmentId)) {
                updatedLines.add(line); // Keep all except the deleted one
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public Optional<Enrollment> getEnrollmentById(String enrollmentId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);

        for (String line : lines) {
            Enrollment enrollment = Enrollment.fromString(line);
            if (enrollment.getEnrollmentId().equals(enrollmentId)) {
                return Optional.of(enrollment); // Found — return it
            }
        }

        return Optional.empty(); // Not found
    }


    @Override
    public List<Enrollment> getAllEnrollments() throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<Enrollment> enrollments = new ArrayList<>();

        for (String line : lines) {
            enrollments.add(Enrollment.fromString(line));
        }

        return enrollments;
    }


    @Override
    public List<Enrollment> getEnrollmentsByStudent(String studentId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<Enrollment> enrollments = new ArrayList<>();

        for (String line : lines) {
            Enrollment enrollment = Enrollment.fromString(line);
            if (enrollment.getStudentId().equals(studentId)) {
                enrollments.add(enrollment); // Only add if it belongs to this student
            }
        }

        return enrollments;
    }


    @Override
    public List<Enrollment> getEnrollmentsByCourse(String courseId, int semester, int year)
            throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<Enrollment> enrollments = new ArrayList<>();

        for (String line : lines) {
            Enrollment enrollment = Enrollment.fromString(line);
            // Only add if course, semester AND year all match
            if (enrollment.getCourseId().equals(courseId)
                    && enrollment.getSemester() == semester
                    && enrollment.getYear() == year) {
                enrollments.add(enrollment);
            }
        }

        return enrollments;
    }


    @Override
    public int countEnrollmentsByStudent(String studentId, int semester, int year)
            throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        int count = 0;

        for (String line : lines) {
            Enrollment enrollment = Enrollment.fromString(line);
            if (enrollment.getStudentId().equals(studentId)
                    && enrollment.getSemester() == semester
                    && enrollment.getYear() == year) {
                count++; // Count every matching enrollment
            }
        }

        return count;
    }
}