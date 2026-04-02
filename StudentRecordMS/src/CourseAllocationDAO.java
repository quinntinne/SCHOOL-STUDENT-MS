

import java.util.List;
import java.util.Optional;

public interface CourseAllocationDAO {

    // ADD a new course allocation
    void addAllocation(CourseAllocation allocation) throws Exception;

    // UPDATE an existing allocation
    void updateAllocation(CourseAllocation allocation) throws Exception;

    // DELETE an allocation by its allocationId
    void deleteAllocation(String allocationId) throws Exception;

    // FIND one allocation by its allocationId
    Optional<CourseAllocation> getAllocationById(String allocationId) throws Exception;

    // GET ALL allocations
    List<CourseAllocation> getAllAllocations() throws Exception;

    // GET ALL allocations for a specific tutor in a given semester and year
    // Used for the Tutor Course Load Report
    List<CourseAllocation> getAllocationsByTutor(String tutorId, int semester, int year)
            throws Exception;

    // GET ALL allocations for a specific course in a given semester and year
    // Used to see which tutor is teaching a course
    List<CourseAllocation> getAllocationsByCourse(String courseId, int semester, int year)
            throws Exception;
}