
import java.util.Objects; 

public class Course {

    // These are the course details
    private String courseId;    
    private String courseName;  
    private int credits;        
    private String department;  


    public Course(String courseId, String courseName, int credits, String department) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
    }

    // -------------------------------------------------------------------------
    // GETTERS — used to READ each field from outside this class
    // -------------------------------------------------------------------------
    public String getCourseId()   { return courseId; }
    public String getCourseName() { return courseName; }
    public int getCredits()       { return credits; }
    public String getDepartment() { return department; }

    // -------------------------------------------------------------------------
    // SETTERS — used to CHANGE each field from outside this class
    // -------------------------------------------------------------------------
    public void setCourseId(String courseId)     { this.courseId = courseId; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setCredits(int credits)          { this.credits = credits; }
    public void setDepartment(String department) { this.department = department; }


    @Override
    public String toString() {
        return String.join(",",
            courseId,
            courseName,
            String.valueOf(credits),
            department
        );
    }


    public static Course fromString(String line) {
        String[] parts = line.split(","); // Split by commas
        if (parts.length == 4) {         // A valid course line must have exactly 4 parts
            return new Course(
                parts[0],                    // courseId
                parts[1],                    // courseName
                Integer.parseInt(parts[2]),  // credits (converted from text to number)
                parts[3]                     // department
            );
        }
        throw new IllegalArgumentException("Invalid Course data format: " + line);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseId, course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}