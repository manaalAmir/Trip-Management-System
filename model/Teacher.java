package tripms.model;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {
    private String teacherId;
    private List<String> managedTripIds;
    private List<String> assignedStudentIds;

    public Teacher(String userId, String username, String email, String password, String teacherId) {
        super(userId, username, email, password, "TEACHER");
        this.teacherId = teacherId;
        this.managedTripIds  = new ArrayList<>();
        this.assignedStudentIds = new ArrayList<>();
    }

    @Override
    public String getDashboardTitle() { return "Teacher Dashboard"; }

    public String getTeacherId()                { return teacherId; }
    public List<String> getManagedTripIds()     { return managedTripIds; }
    public List<String> getAssignedStudentIds() { return assignedStudentIds; }

    public void addManagedTrip(String tripId)       { managedTripIds.add(tripId); }
    public void assignStudent(String studentId)     { assignedStudentIds.add(studentId); }
}
