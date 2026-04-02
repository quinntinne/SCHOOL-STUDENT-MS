
import java.util.List;
import java.util.Optional;

public interface CourseDAO {

    // ADD a new course
    void addCourse(Course course) throws Exception;

    // UPDATE an existing course
    void updateCourse(Course course) throws Exception;

    // DELETE a course by its courseId
    void deleteCourse(String courseId) throws Exception;

    // FIND one course by its courseId
    Optional<Course> getCourseById(String courseId) throws Exception;

    // GET ALL courses
    List<Course> getAllCourses() throws Exception;
}