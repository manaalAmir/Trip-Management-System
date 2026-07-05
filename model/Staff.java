package tripms.model;

public class Staff extends User {
    private String staffId;
    private String department;

    public Staff(String userId, String username, String email, String password,
                 String staffId, String department) {
        super(userId, username, email, password, "STAFF");
        this.staffId    = staffId;
        this.department = department;
    }

    @Override
    public String getDashboardTitle() { return "Staff / Admin Dashboard"; }

    public String getStaffId()    { return staffId; }
    public String getDepartment() { return department; }
}
