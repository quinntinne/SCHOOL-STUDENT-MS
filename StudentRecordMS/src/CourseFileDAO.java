

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseFileDAO implements CourseDAO {

    private static final String FILE_NAME = "courses.txt";


    @Override
    public void addCourse(Course course) throws Exception {
        FileUtil.writeLine(FILE_NAME, course.toString(), true);
    }


    @Override
    public void updateCourse(Course course) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            Course existing = Course.fromString(line);
            if (existing.getCourseId().equals(course.getCourseId())) {
                updatedLines.add(course.toString()); // Replace with updated data
            } else {
                updatedLines.add(line); // Keep unchanged
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public void deleteCourse(String courseId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            Course existing = Course.fromString(line);
            if (!existing.getCourseId().equals(courseId)) {
                updatedLines.add(line); // Keep all courses except the deleted one
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public Optional<Course> getCourseById(String courseId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);

        for (String line : lines) {
            Course course = Course.fromString(line);
            if (course.getCourseId().equals(courseId)) {
                return Optional.of(course); 
            }
        }

        return Optional.empty(); 
    }

 
    @Override
    public List<Course> getAllCourses() throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<Course> courses = new ArrayList<>();

        for (String line : lines) {
            courses.add(Course.fromString(line));
        }

        return courses;
    }
}