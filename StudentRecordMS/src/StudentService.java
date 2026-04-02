

import java.util.List;
import java.util.Optional;

public class StudentService {

    // The DAO that this service will use to save/load data
    // This can be either StudentFileDAO or StudentDatabaseDAO
    // depending on which persistence mode is selected
    private StudentDAO studentDAO;


    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }


    public void addStudent(Student student) throws Exception {
        // Check that the studentId is not empty
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new Exception("Student ID cannot be empty.");
        }

        // Check that the first name is not empty
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new Exception("First name cannot be empty.");
        }

        // Check that the last name is not empty
        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new Exception("Last name cannot be empty.");
        }

        // Check that the date of birth is not null
        if (student.getDateOfBirth() == null) {
            throw new Exception("Date of birth cannot be empty.");
        }

        // Check that a student with this ID does not already exist
        Optional<Student> existing = studentDAO.getStudentById(student.getStudentId());
        if (existing.isPresent()) {
            throw new Exception("A student with ID " + student.getStudentId() +
                                " already exists.");
        }

        // All checks passed — save the student
        studentDAO.addStudent(student);
    }


    public void updateStudent(Student student) throws Exception {
        // Check the student exists before trying to update
        Optional<Student> existing = studentDAO.getStudentById(student.getStudentId());
        if (!existing.isPresent()) {
            throw new Exception("Student with ID " + student.getStudentId() +
                                " not found.");
        }

        // Check names are not empty
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new Exception("First name cannot be empty.");
        }

        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new Exception("Last name cannot be empty.");
        }

        // All checks passed — update the student
        studentDAO.updateStudent(student);
    }


    public void deleteStudent(String studentId) throws Exception {
        // Check the student exists before trying to delete
        Optional<Student> existing = studentDAO.getStudentById(studentId);
        if (!existing.isPresent()) {
            throw new Exception("Student with ID " + studentId + " not found.");
        }

        studentDAO.deleteStudent(studentId);
    }


    public Student getStudentById(String studentId) throws Exception {
        Optional<Student> student = studentDAO.getStudentById(studentId);
        if (!student.isPresent()) {
            throw new Exception("Student with ID " + studentId + " not found.");
        }
        return student.get();
    }


    public List<Student> getAllStudents() throws Exception {
        return studentDAO.getAllStudents();
    }


    public List<Student> searchStudents(String keyword) throws Exception {
        List<Student> allStudents = studentDAO.getAllStudents();
        List<Student> matchingStudents = new java.util.ArrayList<>();

        // Convert keyword to lowercase for case-insensitive search
        String lowerKeyword = keyword.toLowerCase();

        for (Student student : allStudents) {
            // Check if any field contains the keyword
            if (student.getStudentId().toLowerCase().contains(lowerKeyword)
                    || student.getFirstName().toLowerCase().contains(lowerKeyword)
                    || student.getLastName().toLowerCase().contains(lowerKeyword)
                    || student.getMajor().toLowerCase().contains(lowerKeyword)) {
                matchingStudents.add(student);
            }
        }

        return matchingStudents;
    }


    public int countPassedCourses(String studentId, int semester, int year)
            throws Exception {
        return studentDAO.countPassedCourses(studentId, semester, year);
    }


    public boolean hasMetGraduationRequirements(String studentId) throws Exception {
        return studentDAO.hasMetGraduationRequirements(studentId);
    }
}