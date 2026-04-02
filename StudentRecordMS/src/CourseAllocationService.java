
import java.util.List;
import java.util.Optional;

public class CourseAllocationService {

    private CourseAllocationDAO allocationDAO;

    public CourseAllocationService(CourseAllocationDAO allocationDAO) {
        this.allocationDAO = allocationDAO;
    }


    public void addAllocation(CourseAllocation allocation) throws Exception {
        // Check allocationId is not empty
        if (allocation.getAllocationId() == null ||
                allocation.getAllocationId().trim().isEmpty()) {
            throw new Exception("Allocation ID cannot be empty.");
        }

        // Check courseId is not empty
        if (allocation.getCourseId() == null ||
                allocation.getCourseId().trim().isEmpty()) {
            throw new Exception("Course ID cannot be empty.");
        }

        // Check tutorId is not empty
        if (allocation.getTutorId() == null ||
                allocation.getTutorId().trim().isEmpty()) {
            throw new Exception("Tutor ID cannot be empty.");
        }

        // Check no duplicate allocationId exists
        Optional<CourseAllocation> existing = allocationDAO
                .getAllocationById(allocation.getAllocationId());
        if (existing.isPresent()) {
            throw new Exception("An allocation with ID " +
                    allocation.getAllocationId() + " already exists.");
        }

        // Check if this course is already allocated in the same semester and year
        List<CourseAllocation> existingAllocations = allocationDAO
                .getAllocationsByCourse(allocation.getCourseId(),
                        allocation.getSemester(), allocation.getYear());
        if (!existingAllocations.isEmpty()) {
            throw new Exception("Course " + allocation.getCourseId() +
                    " is already allocated for this semester and year.");
        }

        allocationDAO.addAllocation(allocation);
    }


    public void updateAllocation(CourseAllocation allocation) throws Exception {
        Optional<CourseAllocation> existing = allocationDAO
                .getAllocationById(allocation.getAllocationId());
        if (!existing.isPresent()) {
            throw new Exception("Allocation with ID " +
                    allocation.getAllocationId() + " not found.");
        }

        allocationDAO.updateAllocation(allocation);
    }


    public void deleteAllocation(String allocationId) throws Exception {
        Optional<CourseAllocation> existing = allocationDAO
                .getAllocationById(allocationId);
        if (!existing.isPresent()) {
            throw new Exception("Allocation with ID " + allocationId + " not found.");
        }

        allocationDAO.deleteAllocation(allocationId);
    }


    public CourseAllocation getAllocationById(String allocationId) throws Exception {
        Optional<CourseAllocation> allocation = allocationDAO
                .getAllocationById(allocationId);
        if (!allocation.isPresent()) {
            throw new Exception("Allocation with ID " + allocationId + " not found.");
        }
        return allocation.get();
    }


    public List<CourseAllocation> getAllAllocations() throws Exception {
        return allocationDAO.getAllAllocations();
    }


    public List<CourseAllocation> getAllocationsByTutor(String tutorId,
            int semester, int year) throws Exception {
        return allocationDAO.getAllocationsByTutor(tutorId, semester, year);
    }


    public List<CourseAllocation> searchAllocations(String keyword) throws Exception {
        List<CourseAllocation> allAllocations = allocationDAO.getAllAllocations();
        List<CourseAllocation> matchingAllocations = new java.util.ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (CourseAllocation allocation : allAllocations) {
            if (allocation.getAllocationId().toLowerCase().contains(lowerKeyword)
                    || allocation.getCourseId().toLowerCase().contains(lowerKeyword)
                    || allocation.getTutorId().toLowerCase().contains(lowerKeyword)) {
                matchingAllocations.add(allocation);
            }
        }

        return matchingAllocations;
    }
}