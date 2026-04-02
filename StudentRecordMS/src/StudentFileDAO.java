

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentFileDAO implements StudentDAO {

    // The name of the file where student data is stored
    private static final String FILE_NAME = "students.txt";


    @Override
    public void addStudent(Student student) throws Exception {
        // Convert the student object to a text line and append it to the file
        FileUtil.writeLine(FILE_NAME, student.toString(), true);
    }


    @Override
    public void updateStudent(Student student) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME); // Read all lines
        List<String> updatedLines = new ArrayList<>();         // Will hold updated lines

        for (String line : lines) {
            Student existing = Student.fromString(line); // Convert line to Student object
            if (existing.getStudentId().equals(student.getStudentId())) {
                // This is the student we want to update — replace with new data
                updatedLines.add(student.toString());
            } else {
                // This is a different student — keep the line as it is
                updatedLines.add(line);
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines); // Write everything back
    }


    @Override
    public void deleteStudent(String studentId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            Student existing = Student.fromString(line);
            if (!existing.getStudentId().equals(studentId)) {
                // Only keep students that do NOT match the studentId we want to delete
                updatedLines.add(line);
            }
            // If it matches — we simply don't add it, effectively deleting it
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public Optional<Student> getStudentById(String studentId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);

        for (String line : lines) {
            Student student = Student.fromString(line);
            if (student.getStudentId().equals(studentId)) {
                return Optional.of(student); // Found — return the student
            }
        }

        return Optional.empty(); // Not found — return empty
    }


    @Override
    public List<Student> getAllStudents() throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<Student> students = new ArrayList<>();

        for (String line : lines) {
            students.add(Student.fromString(line)); // Convert each line to a Student
        }

        return students;
    }


    @Override
    public int countPassedCourses(String studentId, int semester, int year)
            throws Exception {
        // Read all enrollments and results from their files
        List<String> enrollmentLines = FileUtil.readAllLines("enrollments.txt");
        List<String> resultLines = FileUtil.readAllLines("examination_results.txt");

        int passedCount = 0; // Counter for passed courses

        for (String eLine : enrollmentLines) {
            Enrollment enrollment = Enrollment.fromString(eLine);

            // Check if this enrollment belongs to our student in the right semester/year
            if (enrollment.getStudentId().equals(studentId)
                    && enrollment.getSemester() == semester
                    && enrollment.getYear() == year) {

                // Now check if there is a passing result for this enrollment
                for (String rLine : resultLines) {
                    ExaminationResult result = ExaminationResult.fromString(rLine);
                    if (result.getEnrollmentId().equals(enrollment.getEnrollmentId())
                            && result.isPassed()) {
                        passedCount++; // This course was passed
                        break;         // No need to check more results for this enrollment
                    }
                }
            }
        }

        return passedCount;
    }


    @Override
    public boolean hasMetGraduationRequirements(String studentId) throws Exception {
        List<String> enrollmentLines = FileUtil.readAllLines("enrollments.txt");
        List<String> resultLines     = FileUtil.readAllLines("examination_results.txt");
        List<String> courseLines     = FileUtil.readAllLines("courses.txt");

        int totalCredits = 0; // Counter for total credits earned

        for (String eLine : enrollmentLines) {
            Enrollment enrollment = Enrollment.fromString(eLine);

            // Only look at enrollments belonging to this student
            if (enrollment.getStudentId().equals(studentId)) {

                // Check if the student passed this enrollment
                for (String rLine : resultLines) {
                    ExaminationResult result = ExaminationResult.fromString(rLine);

                    if (result.getEnrollmentId().equals(enrollment.getEnrollmentId())
                            && result.isPassed()) {

                        // Find the course to get its credit value
                        for (String cLine : courseLines) {
                            Course course = Course.fromString(cLine);
                            if (course.getCourseId().equals(enrollment.getCourseId())) {
                                totalCredits += course.getCredits(); // Add credits
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

        return totalCredits >= 120; 
    }
}