package tripms.model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private String studentId;
    private List<String> bookingIds;
    private List<String> tripHistory;

    public Student(String userId, String username, String email, String password, String studentId) {
        super(userId, username, email, password, "STUDENT");
        this.studentId = studentId;
        this.bookingIds = new ArrayList<>();
        this.tripHistory = new ArrayList<>();
    }

    @Override
    public String getDashboardTitle() { return "Student Dashboard"; }

    public String getStudentId()         { return studentId; }
    public List<String> getBookingIds()  { return bookingIds; }
    public List<String> getTripHistory() { return tripHistory; }

    public void addBooking(String bookingId)    { bookingIds.add(bookingId); }
    public void removeBooking(String bookingId) { bookingIds.remove(bookingId); }
    public void addTripHistory(String tripId)   { tripHistory.add(tripId); }
}
