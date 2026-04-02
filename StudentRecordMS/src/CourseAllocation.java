

import java.util.Objects;

public class CourseAllocation {

    // These are the allocation details
    private String allocationId;
    private String courseId;      
    private String tutorId;      
    private int semester;         
    private int year;             


    public CourseAllocation(String allocationId, String courseId,
                            String tutorId, int semester, int year) {
        this.allocationId = allocationId;
        this.courseId = courseId;
        this.tutorId = tutorId;
        this.semester = semester;
        this.year = year;
    }

    // -------------------------------------------------------------------------
    // GETTERS — used to READ each field from outside this class
    // -------------------------------------------------------------------------
    public String getAllocationId() { return allocationId; }
    public String getCourseId()     { return courseId; }
    public String getTutorId()      { return tutorId; }
    public int getSemester()        { return semester; }
    public int getYear()            { return year; }

    // -------------------------------------------------------------------------
    // SETTERS — used to CHANGE each field from outside this class
    // -------------------------------------------------------------------------
    public void setAllocationId(String allocationId) { this.allocationId = allocationId; }
    public void setCourseId(String courseId)         { this.courseId = courseId; }
    public void setTutorId(String tutorId)           { this.tutorId = tutorId; }
    public void setSemester(int semester)             { this.semester = semester; }
    public void setYear(int year)                     { this.year = year; }


    @Override
    public String toString() {
        return String.join(",",
            allocationId,
            courseId,
            tutorId,
            String.valueOf(semester),
            String.valueOf(year)
        );
    }


    public static CourseAllocation fromString(String line) {
        String[] parts = line.split(","); 
        if (parts.length == 5) {         
            return new CourseAllocation(
                parts[0],                    // allocationId
                parts[1],                    // courseId
                parts[2],                    // tutorId
                Integer.parseInt(parts[3]),  // semester (text to number)
                Integer.parseInt(parts[4])   // year (text to number)
            );
        }
        throw new IllegalArgumentException("Invalid CourseAllocation data format: " + line);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseAllocation that = (CourseAllocation) o;
        return Objects.equals(allocationId, that.allocationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allocationId);
    }
}