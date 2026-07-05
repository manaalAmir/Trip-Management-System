package tripms.model;

public class Driver {
    private String driverId;
    private String name;
    private String licenseNumber;
    private String contactNumber;
    private String assignedTripId;

    public Driver(String driverId, String name, String licenseNumber, String contactNumber) {
        this.driverId      = driverId;
        this.name          = name;
        this.licenseNumber = licenseNumber;
        this.contactNumber = contactNumber;
        this.assignedTripId = "";
    }

    public String getDriverId()      { return driverId; }
    public String getName()          { return name; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getContactNumber() { return contactNumber; }
    public String getAssignedTripId(){ return assignedTripId; }
    public void setAssignedTripId(String t) { this.assignedTripId = t; }
    public void setName(String n)          { this.name = n; }
    public void setContactNumber(String c) { this.contactNumber = c; }

    @Override
    public String toString() { return name + " [" + licenseNumber + "]"; }
}
