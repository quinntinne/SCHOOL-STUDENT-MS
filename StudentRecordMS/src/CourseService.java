

import java.util.List;
import java.util.Optional;

public class CourseService {

    private CourseDAO courseDAO;

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }


    public void addCourse(Course course) throws Exception {
        // Check courseId is not empty
        if (course.getCourseId() == null || course.getCourseId().trim().isEmpty()) {
            throw new Exception("Course ID cannot be empty.");
        }

        // Check course name is not empty
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new Exception("Course name cannot be empty.");
        }

        // Check credits is a positive number
        if (course.getCredits() <= 0) {
            throw new Exception("Credits must be a positive number.");
        }

        // Check no duplicate courseId exists
        Optional<Course> existing = courseDAO.getCourseById(course.getCourseId());
        if (existing.isPresent()) {
            throw new Exception("A course with ID " + course.getCourseId() +
                                " already exists.");
        }

        courseDAO.addCourse(course);
    }


    public void updateCourse(Course course) throws Exception {
        Optional<Course> existing = courseDAO.getCourseById(course.getCourseId());
        if (!existing.isPresent()) {
            throw new Exception("Course with ID " + course.getCourseId() + " not found.");
        }

        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new Exception("Course name cannot be empty.");
        }

        if (course.getCredits() <= 0) {
            throw new Exception("Credits must be a positive number.");
        }

        courseDAO.updateCourse(course);
    }


    public void deleteCourse(String courseId) throws Exception {
        Optional<Course> existing = courseDAO.getCourseById(courseId);
        if (!existing.isPresent()) {
            throw new Exception("Course with ID " + courseId + " not found.");
        }

        courseDAO.deleteCourse(courseId);
    }


    public Course getCourseById(String courseId) throws Exception {
        Optional<Course> course = courseDAO.getCourseById(courseId);
        if (!course.isPresent()) {
            throw new Exception("Course with ID " + courseId + " not found.");
        }
        return course.get();
    }


    public List<Course> getAllCourses() throws Exception {
        return courseDAO.getAllCourses();
    }


    public List<Course> searchCourses(String keyword) throws Exception {
        List<Course> allCourses = courseDAO.getAllCourses();
        List<Course> matchingCourses = new java.util.ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Course course : allCourses) {
            if (course.getCourseId().toLowerCase().contains(lowerKeyword)
                    || course.getCourseName().toLowerCase().contains(lowerKeyword)
                    || course.getDepartment().toLowerCase().contains(lowerKeyword)) {
                matchingCourses.add(course);
            }
        }

        return matchingCourses;
    }
}