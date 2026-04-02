

public class AcademicProgressionService {


    private StudentDAO studentDAO;
    private StudentService studentService;
    private EnrollmentService enrollmentService;


    public AcademicProgressionService(StudentDAO studentDAO,
                                      StudentService studentService,
                                      EnrollmentService enrollmentService) {
        this.studentDAO = studentDAO;
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }


    public void promoteStudent(String studentId) throws Exception {

        // Step 1: Get the student's current details
        Student student = studentService.getStudentById(studentId);

        // Step 2: Check the student is active — cannot promote a graduated student
        if (student.getAcademicStatus().equals("Graduated") ||
                student.getAcademicStatus().equals("Cleared for Graduation")) {
            throw new Exception("Student " + studentId +
                    " has already completed their studies.");
        }

        int currentSemester = student.getCurrentSemester();
        int currentYear = student.getCurrentYear();

        // Step 3: Check minimum course load
        // The student must have enrolled in at least 10 courses this semester
        boolean hasMinLoad = enrollmentService.hasMinimumCourseLoad(
                studentId, currentSemester, currentYear);

        if (!hasMinLoad) {
            throw new Exception("Student " + studentId +
                    " has not registered for the minimum of 10 courses " +
                    "in semester " + currentSemester + " year " + currentYear + ".");
        }

        // Step 4: Check if the student passed all required courses
        // Count how many courses they passed this semester
        int passedCourses = studentDAO.countPassedCourses(
                studentId, currentSemester, currentYear);

        if (passedCourses < 10) {
            // Student failed — put them on probation instead of promoting
            student.setAcademicStatus("On Probation");
            studentDAO.updateStudent(student);
            throw new Exception("Student " + studentId +
                    " has only passed " + passedCourses + " course(s). " +
                    "A minimum of 10 passed courses is required for promotion. " +
                    "Student status has been set to 'On Probation'.");
        }

        // Step 5: All checks passed — promote the student
        if (currentSemester == 1) {
            // Move from semester 1 to semester 2 of the same year
            student.setCurrentSemester(2);
        } else {
            // Move from semester 2 to semester 1 of the next year
            student.setCurrentSemester(1);
            student.setCurrentYear(currentYear + 1);
        }

        // Update status back to Active in case they were on probation before
        student.setAcademicStatus("Active");

        // Step 6: Save the updated student details
        studentDAO.updateStudent(student);
    }


    public void clearForGraduation(String studentId) throws Exception {

        // Step 1: Get the student's current details
        Student student = studentService.getStudentById(studentId);

        // Step 2: Check the student is not already graduated
        if (student.getAcademicStatus().equals("Graduated")) {
            throw new Exception("Student " + studentId +
                    " has already graduated.");
        }

        if (student.getAcademicStatus().equals("Cleared for Graduation")) {
            throw new Exception("Student " + studentId +
                    " has already been cleared for graduation.");
        }

        // Step 3: Check if student is in their final year
        if (student.getCurrentYear() < 4) {
            throw new Exception("Student " + studentId +
                    " is in year " + student.getCurrentYear() +
                    ". Only year 4 students can be cleared for graduation.");
        }

        // Step 4: Check if the student has met graduation requirements
        // (earned at least 120 credits from passed courses)
        boolean metRequirements = studentDAO.hasMetGraduationRequirements(studentId);

        if (!metRequirements) {
            throw new Exception("Student " + studentId +
                    " has not yet earned the required 120 credits for graduation.");
        }

        // Step 5: All checks passed — clear the student for graduation
        student.setAcademicStatus("Cleared for Graduation");
        studentDAO.updateStudent(student);
    }


    public java.util.List<Student> getStudentsOnProbation() throws Exception {
        java.util.List<Student> allStudents = studentService.getAllStudents();
        java.util.List<Student> onProbation = new java.util.ArrayList<>();

        for (Student student : allStudents) {
            if (student.getAcademicStatus().equals("On Probation")) {
                onProbation.add(student);
            }
        }

        return onProbation;
    }


    public java.util.List<Student> getStudentsEligibleForGraduation() throws Exception {
        java.util.List<Student> allStudents = studentService.getAllStudents();
        java.util.List<Student> eligible = new java.util.ArrayList<>();

        for (Student student : allStudents) {
            // Check if they are in year 4 and have met credit requirements
            if (student.getCurrentYear() >= 4 &&
                    studentDAO.hasMetGraduationRequirements(student.getStudentId())) {
                eligible.add(student);
            }
        }

        return eligible;
    }
}