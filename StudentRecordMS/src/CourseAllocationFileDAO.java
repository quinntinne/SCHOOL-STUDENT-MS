

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseAllocationFileDAO implements CourseAllocationDAO {

    private static final String FILE_NAME = "course_allocations.txt";


    @Override
    public void addAllocation(CourseAllocation allocation) throws Exception {
        FileUtil.writeLine(FILE_NAME, allocation.toString(), true);
    }


    @Override
    public void updateAllocation(CourseAllocation allocation) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            CourseAllocation existing = CourseAllocation.fromString(line);
            if (existing.getAllocationId().equals(allocation.getAllocationId())) {
                updatedLines.add(allocation.toString()); // Replace with updated data
            } else {
                updatedLines.add(line); // Keep unchanged
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public void deleteAllocation(String allocationId) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            CourseAllocation existing = CourseAllocation.fromString(line);
            if (!existing.getAllocationId().equals(allocationId)) {
                updatedLines.add(line); // Keep all except the deleted one
            }
        }

        FileUtil.writeAllLines(FILE_NAME, updatedLines);
    }


    @Override
    public Optional<CourseAllocation> getAllocationById(String allocationId)
            throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);

        for (String line : lines) {
            CourseAllocation allocation = CourseAllocation.fromString(line);
            if (allocation.getAllocationId().equals(allocationId)) {
                return Optional.of(allocation); // Found — return it
            }
        }

        return Optional.empty(); // Not found
    }


    @Override
    public List<CourseAllocation> getAllAllocations() throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<CourseAllocation> allocations = new ArrayList<>();

        for (String line : lines) {
            allocations.add(CourseAllocation.fromString(line));
        }

        return allocations;
    }


    @Override
    public List<CourseAllocation> getAllocationsByTutor(String tutorId,
            int semester, int year) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<CourseAllocation> allocations = new ArrayList<>();

        for (String line : lines) {
            CourseAllocation allocation = CourseAllocation.fromString(line);
            // Only add if tutor, semester AND year all match
            if (allocation.getTutorId().equals(tutorId)
                    && allocation.getSemester() == semester
                    && allocation.getYear() == year) {
                allocations.add(allocation);
            }
        }

        return allocations;
    }


    @Override
    public List<CourseAllocation> getAllocationsByCourse(String courseId,
            int semester, int year) throws Exception {
        List<String> lines = FileUtil.readAllLines(FILE_NAME);
        List<CourseAllocation> allocations = new ArrayList<>();

        for (String line : lines) {
            CourseAllocation allocation = CourseAllocation.fromString(line);
            // Only add if course, semester AND year all match
            if (allocation.getCourseId().equals(courseId)
                    && allocation.getSemester() == semester
                    && allocation.getYear() == year) {
                allocations.add(allocation);
            }
        }

        return allocations;
    }
}