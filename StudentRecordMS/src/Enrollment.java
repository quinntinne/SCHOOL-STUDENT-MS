
import java.time.LocalDate; 
import java.util.Objects;   

public class Enrollment {

    // These are the enrollment details
    private String enrollmentId;      
    private String studentId;          
    private String courseId;            
    private int semester;               
    private int year;                   
    private LocalDate enrollmentDate;    


    public Enrollment(String enrollmentId, String studentId, String courseId,
                      int semester, int year, LocalDate enrollmentDate) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.semester = semester;
        this.year = year;
        this.enrollmentDate = enrollmentDate;
    }

    // -------------------------------------------------------------------------
    // GETTERS — used to READ each field from outside this class
    // -------------------------------------------------------------------------
    public String getEnrollmentId()       { return enrollmentId; }
    public String getStudentId()          { return studentId; }
    public String getCourseId()           { return courseId; }
    public int getSemester()              { return semester; }
    public int getYear()                  { return year; }
    public LocalDate getEnrollmentDate()  { return enrollmentDate; }

    // -------------------------------------------------------------------------
    // SETTERS — used to CHANGE each field from outside this class
    // -------------------------------------------------------------------------
    public void setEnrollmentId(String enrollmentId)       { this.enrollmentId = enrollmentId; }
    public void setStudentId(String studentId)             { this.studentId = studentId; }
    public void setCourseId(String courseId)               { this.courseId = courseId; }
    public void setSemester(int semester)                   { this.semester = semester; }
    public void setYear(int year)                           { this.year = year; }
    public void setEnrollmentDate(LocalDate enrollmentDate){ this.enrollmentDate = enrollmentDate; }


    @Override
    public String toString() {
        return String.join(",",
            enrollmentId,
            studentId,
            courseId,
            String.valueOf(semester),
            String.valueOf(year),
            enrollmentDate.toString()
        );
    }


    public static Enrollment fromString(String line) {
        String[] parts = line.split(","); 
        if (parts.length == 6) {         
            return new Enrollment(
                parts[0],                       // enrollmentId
                parts[1],                       // studentId
                parts[2],                       // courseId
                Integer.parseInt(parts[3]),     // semester (text to number)
                Integer.parseInt(parts[4]),     // year (text to number)
                LocalDate.parse(parts[5])       // enrollmentDate (text to date)
            );
        }
        throw new IllegalArgumentException("Invalid Enrollment data format: " + line);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return Objects.equals(enrollmentId, that.enrollmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
    }
}