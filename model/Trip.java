package tripms.model;

import java.util.ArrayList;
import java.util.List;

public class Trip {
    public enum Status { DRAFT, SCHEDULED, OPEN, BOOKING_CLOSED, IN_PROGRESS, COMPLETED, CANCELLED }

    private String tripId;
    private String tripName;
    private String destination;
    private String startDate;
    private String endDate;
    private int    durationDays;
    private double budgetPerStudent;
    private int    participationLimit;
    private int    currentParticipants;
    private Status status;
    private String createdByTeacherId;
    private String travelMode;      // Bus, Van, etc.
    private String schedule;
    private String precautions;
    private String accommodationInfo;
    private int    busCount;
    private List<String> enrolledStudentIds;
    private List<String> teacherIds;

    public Trip(String tripId, String tripName, String destination,
                String startDate, String endDate, int durationDays,
                double budgetPerStudent, int participationLimit,
                String createdByTeacherId, String travelMode) {
        this.tripId              = tripId;
        this.tripName            = tripName;
        this.destination         = destination;
        this.startDate           = startDate;
        this.endDate             = endDate;
        this.durationDays        = durationDays;
        this.budgetPerStudent    = budgetPerStudent;
        this.participationLimit  = participationLimit;
        this.currentParticipants = 0;
        this.status              = Status.DRAFT;
        this.createdByTeacherId  = createdByTeacherId;
        this.travelMode          = travelMode;
        this.schedule            = "";
        this.precautions         = "Always stay with your group.\nCarry your ID at all times.\nFollow teacher instructions.";
        this.accommodationInfo   = "";
        this.busCount            = 1;
        this.enrolledStudentIds  = new ArrayList<>();
        this.teacherIds          = new ArrayList<>();
    }

    public boolean isFull()   { return currentParticipants >= participationLimit; }
    public boolean isOpen()   { return status == Status.OPEN && !isFull(); }

    public boolean enrollStudent(String studentId) {
        if (isFull() || enrolledStudentIds.contains(studentId)) return false;
        enrolledStudentIds.add(studentId);
        currentParticipants++;
        return true;
    }

    public boolean unenrollStudent(String studentId) {
        if (enrolledStudentIds.remove(studentId)) {
            currentParticipants--;
            return true;
        }
        return false;
    }

    // --- Getters ---
    public String getTripId()             { return tripId; }
    public String getTripName()           { return tripName; }
    public String getDestination()        { return destination; }
    public String getStartDate()          { return startDate; }
    public String getEndDate()            { return endDate; }
    public int    getDurationDays()       { return durationDays; }
    public double getBudgetPerStudent()   { return budgetPerStudent; }
    public int    getParticipationLimit() { return participationLimit; }
    public int    getCurrentParticipants(){ return currentParticipants; }
    public Status getStatus()             { return status; }
    public String getCreatedByTeacherId() { return createdByTeacherId; }
    public String getTravelMode()         { return travelMode; }
    public String getSchedule()           { return schedule; }
    public String getPrecautions()        { return precautions; }
    public String getAccommodationInfo()  { return accommodationInfo; }
    public int    getBusCount()           { return busCount; }
    public List<String> getEnrolledStudentIds() { return enrolledStudentIds; }
    public List<String> getTeacherIds()         { return teacherIds; }

    // --- Setters ---
    public void setTripName(String n)          { this.tripName = n; }
    public void setDestination(String d)       { this.destination = d; }
    public void setStartDate(String d)         { this.startDate = d; }
    public void setEndDate(String d)           { this.endDate = d; }
    public void setDurationDays(int d)         { this.durationDays = d; }
    public void setBudgetPerStudent(double b)  { this.budgetPerStudent = b; }
    public void setParticipationLimit(int l)   { this.participationLimit = l; }
    public void setStatus(Status s)            { this.status = s; }
    public void setTravelMode(String m)        { this.travelMode = m; }
    public void setSchedule(String s)          { this.schedule = s; }
    public void setPrecautions(String p)       { this.precautions = p; }
    public void setAccommodationInfo(String a) { this.accommodationInfo = a; }
    public void setBusCount(int b)             { this.busCount = b; }
    public void addTeacher(String teacherId)   { teacherIds.add(teacherId); }

    @Override
    public String toString() { return tripName + " → " + destination; }
}
