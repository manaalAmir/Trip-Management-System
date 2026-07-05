package tripms.model;

public class Booking {
    public enum BookingStatus { CONFIRMED, CANCELLED, PENDING }
    public enum PaymentStatus { PAID, REFUNDED, FAILED, PENDING }

    private String bookingId;
    private String studentId;
    private String tripId;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private double amountPaid;
    private String paymentMethod;  // Card, Cash, Online
    private String bookingDate;
    private String seatNumber;

    public Booking(String bookingId, String studentId, String tripId,
                   double amountPaid, String paymentMethod, String bookingDate) {
        this.bookingId     = bookingId;
        this.studentId     = studentId;
        this.tripId        = tripId;
        this.amountPaid    = amountPaid;
        this.paymentMethod = paymentMethod;
        this.bookingDate   = bookingDate;
        this.bookingStatus = BookingStatus.CONFIRMED;
        this.paymentStatus = PaymentStatus.PAID;
        this.seatNumber    = "S-" + bookingId;
    }

    public void cancel() {
        this.bookingStatus = BookingStatus.CANCELLED;
        this.paymentStatus = PaymentStatus.REFUNDED;
    }

    // Getters
    public String getBookingId()         { return bookingId; }
    public String getStudentId()         { return studentId; }
    public String getTripId()            { return tripId; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public double getAmountPaid()        { return amountPaid; }
    public String getPaymentMethod()     { return paymentMethod; }
    public String getBookingDate()       { return bookingDate; }
    public String getSeatNumber()        { return seatNumber; }

    public void setBookingStatus(BookingStatus s) { this.bookingStatus = s; }
    public void setPaymentStatus(PaymentStatus s) { this.paymentStatus = s; }
}
