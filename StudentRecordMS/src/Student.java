
import java.time.LocalDate; // We import this to use dates (date of birth)
import java.util.Objects;   // We import this for the equals() and hashCode() methods

public class Student {

    // These are the student's details (called "fields" or "attributes")
    // "private" means only this class can directly access them
    private String studentId;       
    private String firstName;       
    private String lastName;        
    private LocalDate dateOfBirth;  
    private String major;           
    private int currentSemester;    
    private int currentYear;        
    private String academicStatus;  


    public Student(String studentId, String firstName, String lastName,
                   LocalDate dateOfBirth, String major,
                   int currentSemester, int currentYear, String academicStatus) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.major = major;
        this.currentSemester = currentSemester;
        this.currentYear = currentYear;
        this.academicStatus = academicStatus;
    }

    // -------------------------------------------------------------------------
    // GETTERS — used to READ the value of each field from outside this class
    // -------------------------------------------------------------------------
    public String getStudentId()       { return studentId; }
    public String getFirstName()       { return firstName; }
    public String getLastName()        { return lastName; }
    public LocalDate getDateOfBirth()  { return dateOfBirth; }
    public String getMajor()           { return major; }
    public int getCurrentSemester()    { return currentSemester; }
    public int getCurrentYear()        { return currentYear; }
    public String getAcademicStatus()  { return academicStatus; }

    // -------------------------------------------------------------------------
    // SETTERS — used to CHANGE the value of each field from outside this class
    // -------------------------------------------------------------------------
    public void setStudentId(String studentId)           { this.studentId = studentId; }
    public void setFirstName(String firstName)           { this.firstName = firstName; }
    public void setLastName(String lastName)             { this.lastName = lastName; }
    public void setDateOfBirth(LocalDate dateOfBirth)   { this.dateOfBirth = dateOfBirth; }
    public void setMajor(String major)                   { this.major = major; }
    public void setCurrentSemester(int currentSemester) { this.currentSemester = currentSemester; }
    public void setCurrentYear(int currentYear)         { this.currentYear = currentYear; }
    public void setAcademicStatus(String academicStatus){ this.academicStatus = academicStatus; }


    @Override
    public String toString() {
        return String.join(",",
            studentId,
            firstName,
            lastName,
            dateOfBirth.toString(),
            major,
            String.valueOf(currentSemester),
            String.valueOf(currentYear),
            academicStatus
        );
    }



    public static Student fromString(String line) {
        String[] parts = line.split(","); 
        if (parts.length == 8) {         // A valid student line must have exactly 8 parts
            return new Student(
                parts[0],                        
                parts[1],                       
                parts[2],                        
                LocalDate.parse(parts[3]),       
                parts[4],                        
                Integer.parseInt(parts[5]),      
                Integer.parseInt(parts[6]),      
                parts[7]                         
            );
        }
        // If the line doesn't have 8 parts, it's invalid — throw an error
        throw new IllegalArgumentException("Invalid Student data format: " + line);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}