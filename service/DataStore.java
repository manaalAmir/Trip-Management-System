package tripms.service;

import tripms.model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central in-memory data store — acts as the repository / service layer.
 * Maps to: association relationships in the class diagram.
 */
public class DataStore {
    private static DataStore instance;

    private Map<String, User>    users    = new LinkedHashMap<>();
    private Map<String, Trip>    trips    = new LinkedHashMap<>();
    private Map<String, Booking> bookings = new LinkedHashMap<>();
    private Map<String, Driver>  drivers  = new LinkedHashMap<>();

    private int userCounter    = 100;
    private int tripCounter    = 1;
    private int bookingCounter = 1;
    private int driverCounter  = 1;

    private DataStore() { seed(); }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ── Seeding ─────────────────────────────────────────────────────────────
    private void seed() {
        // Users
        Student s1 = new Student("U101","ali","ali@school.edu","pass123","STU-101");
        Student s2 = new Student("U102","sara","sara@school.edu","pass123","STU-102");
        Student s3 = new Student("U103","usman","usman@school.edu","pass123","STU-103");
        Teacher t1 = new Teacher("U201","MrKhan","khan@school.edu","teach123","TCH-201");
        Teacher t2 = new Teacher("U202","MsAyesha","ayesha@school.edu","teach123","TCH-202");
        Staff   st = new Staff("U301","Admin","admin@school.edu","admin123","STF-301","Administration");

        Arrays.asList(s1,s2,s3,t1,t2,st).forEach(u -> users.put(u.getUserId(), u));

        // Trips
        Trip trip1 = new Trip("T001","Murree Snow Trip","Murree",
                "2025-12-20","2025-12-22",3,4500,30,"U201","Bus");
        trip1.setStatus(Trip.Status.OPEN);
        trip1.setSchedule("Day 1: Depart 6AM, Arrive 11AM, Explore Mall Road\nDay 2: Snow activities, Local food\nDay 3: Return 9AM");
        trip1.setAccommodationInfo("Hotel Snow Pearl, Murree — 2 students per room");
        trip1.addTeacher("U201");

        Trip trip2 = new Trip("T002","Lahore Heritage Tour","Lahore",
                "2026-02-10","2026-02-12",3,3500,40,"U202","Van");
        trip2.setStatus(Trip.Status.SCHEDULED);
        trip2.setSchedule("Day 1: Lahore Fort, Badshahi Mosque\nDay 2: Shalimar Gardens, Food Street\nDay 3: Return");
        trip2.setAccommodationInfo("PTDC Motel, Lahore — 3 students per room");
        trip2.addTeacher("U202");

        Trip trip3 = new Trip("T003","Karachi Beach Excursion","Karachi",
                "2026-03-05","2026-03-06",2,2800,50,"U201","Bus");
        trip3.setStatus(Trip.Status.COMPLETED);
        trip3.addTeacher("U201");

        Arrays.asList(trip1,trip2,trip3).forEach(t -> trips.put(t.getTripId(), t));

        // Enroll some students in trip1
        trip1.enrollStudent("U101");
        trip1.enrollStudent("U102");
        s1.addBooking("B001"); s1.addTripHistory("T003");
        s2.addBooking("B002");

        // Bookings
        Booking b1 = new Booking("B001","U101","T001",4500,"Card","2025-11-01");
        Booking b2 = new Booking("B002","U102","T001",4500,"Online","2025-11-02");
        bookings.put("B001", b1);
        bookings.put("B002", b2);

        // Drivers
        Driver d1 = new Driver("D001","Rafiq Ahmed","LHR-12345","0300-1234567");
        d1.setAssignedTripId("T001");
        Driver d2 = new Driver("D002","Tariq Mehmood","ISB-67890","0311-9876543");
        d2.setAssignedTripId("T002");
        drivers.put("D001", d1);
        drivers.put("D002", d2);
    }

    // ── Auth ─────────────────────────────────────────────────────────────────
    public User login(String email, String password) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email)
                          && u.getPassword().equals(password))
                .findFirst().orElse(null);
    }

    public boolean emailExists(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    // ── User CRUD ────────────────────────────────────────────────────────────
    public User registerUser(String username, String email, String password, String role) {
        if (emailExists(email)) return null;
        String uid = "U" + (++userCounter);
        User u;
        switch (role) {
            case "TEACHER": u = new Teacher(uid, username, email, password, "TCH-"+userCounter); break;
            case "STAFF":   u = new Staff(uid, username, email, password, "STF-"+userCounter, "General"); break;
            default:        u = new Student(uid, username, email, password, "STU-"+userCounter);
        }
        users.put(uid, u);
        return u;
    }

    // ── Trip CRUD ────────────────────────────────────────────────────────────
    public Trip createTrip(String name, String dest, String start, String end,
                           int duration, double budget, int limit,
                           String teacherId, String travelMode) {
        String tid = "T" + String.format("%03d", ++tripCounter);
        Trip t = new Trip(tid, name, dest, start, end, duration, budget, limit, teacherId, travelMode);
        t.addTeacher(teacherId);
        trips.put(tid, t);
        if (users.get(teacherId) instanceof Teacher)
            ((Teacher) users.get(teacherId)).addManagedTrip(tid);
        return t;
    }

    public boolean updateTrip(Trip t) { trips.put(t.getTripId(), t); return true; }

    // ── Booking ──────────────────────────────────────────────────────────────
    public Booking bookSeat(String studentId, String tripId, String paymentMethod) {
        Trip trip = trips.get(tripId);
        if (trip == null || !trip.isOpen()) return null;
        // Check already booked
        boolean alreadyBooked = bookings.values().stream()
                .anyMatch(b -> b.getStudentId().equals(studentId)
                            && b.getTripId().equals(tripId)
                            && b.getBookingStatus() == Booking.BookingStatus.CONFIRMED);
        if (alreadyBooked) return null;

        if (!trip.enrollStudent(studentId)) return null;

        String bid = "B" + String.format("%03d", ++bookingCounter);
        String date = LocalDate.now().toString();
        Booking b = new Booking(bid, studentId, tripId, trip.getBudgetPerStudent(), paymentMethod, date);
        bookings.put(bid, b);
        if (users.get(studentId) instanceof Student)
            ((Student) users.get(studentId)).addBooking(bid);
        return b;
    }

    public boolean cancelBooking(String bookingId) {
        Booking b = bookings.get(bookingId);
        if (b == null || b.getBookingStatus() == Booking.BookingStatus.CANCELLED) return false;
        b.cancel();
        Trip t = trips.get(b.getTripId());
        if (t != null) t.unenrollStudent(b.getStudentId());
        return true;
    }

    // ── Queries ──────────────────────────────────────────────────────────────
    public List<Trip>    getAllTrips()    { return new ArrayList<>(trips.values()); }
    public List<User>    getAllUsers()    { return new ArrayList<>(users.values()); }
    public List<Booking> getAllBookings() { return new ArrayList<>(bookings.values()); }
    public List<Driver>  getAllDrivers()  { return new ArrayList<>(drivers.values()); }

    public Trip    getTrip(String id)    { return trips.get(id); }
    public User    getUser(String id)    { return users.get(id); }
    public Booking getBooking(String id) { return bookings.get(id); }

    public List<Trip> getOpenTrips() {
        return trips.values().stream()
                .filter(t -> t.getStatus() == Trip.Status.OPEN)
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsForStudent(String studentId) {
        return bookings.values().stream()
                .filter(b -> b.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsForTrip(String tripId) {
        return bookings.values().stream()
                .filter(b -> b.getTripId().equals(tripId))
                .collect(Collectors.toList());
    }

    public List<Trip> getTripHistoryForStudent(String studentId) {
        Student s = (Student) users.get(studentId);
        if (s == null) return new ArrayList<>();
        return s.getTripHistory().stream()
                .map(trips::get).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Trip> getTripsForTeacher(String teacherId) {
        return trips.values().stream()
                .filter(t -> t.getTeacherIds().contains(teacherId))
                .collect(Collectors.toList());
    }

    public void addDriver(String name, String license, String contact) {
        String did = "D" + String.format("%03d", ++driverCounter);
        drivers.put(did, new Driver(did, name, license, contact));
    }

    public String generateTripReport(String tripId) {
        Trip t = trips.get(tripId);
        if (t == null) return "Trip not found.";
        List<Booking> bList = getBookingsForTrip(tripId);
        double totalRevenue = bList.stream()
                .filter(b -> b.getBookingStatus() == Booking.BookingStatus.CONFIRMED)
                .mapToDouble(Booking::getAmountPaid).sum();
        StringBuilder sb = new StringBuilder();
        sb.append("=== TRIP REPORT ===\n");
        sb.append("Trip    : ").append(t.getTripName()).append("\n");
        sb.append("Dest    : ").append(t.getDestination()).append("\n");
        sb.append("Dates   : ").append(t.getStartDate()).append(" to ").append(t.getEndDate()).append("\n");
        sb.append("Status  : ").append(t.getStatus()).append("\n");
        sb.append("Enrolled: ").append(t.getCurrentParticipants()).append(" / ").append(t.getParticipationLimit()).append("\n");
        sb.append("Budget/Student: PKR ").append(t.getBudgetPerStudent()).append("\n");
        sb.append("Total Revenue : PKR ").append(totalRevenue).append("\n");
        sb.append("Travel  : ").append(t.getTravelMode()).append(" (").append(t.getBusCount()).append(" buses)\n");
        sb.append("\n--- Bookings ---\n");
        for (Booking b : bList) {
            User u = users.get(b.getStudentId());
            sb.append(String.format("  %s | %s | PKR %.0f | %s | %s\n",
                    b.getBookingId(),
                    u != null ? u.getUsername() : b.getStudentId(),
                    b.getAmountPaid(),
                    b.getPaymentMethod(),
                    b.getBookingStatus()));
        }
        return sb.toString();
    }
}
