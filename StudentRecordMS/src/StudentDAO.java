import java.util.List;     
import java.util.Optional; 

public interface StudentDAO {

    // ADD a new student to the data source (file or database)
    void addStudent(Student student) throws Exception;

    // UPDATE an existing student's details
    void updateStudent(Student student) throws Exception;

    // DELETE a student using their studentId
    void deleteStudent(String studentId) throws Exception;


    Optional<Student> getStudentById(String studentId) throws Exception;

    // GET ALL students from the data source
    List<Student> getAllStudents() throws Exception;


    int countPassedCourses(String studentId, int semester, int year) throws Exception;


    boolean hasMetGraduationRequirements(String studentId) throws Exception;
}