package tripms.model;

public abstract class User {
    protected String userId;
    protected String username;
    protected String email;
    protected String password;
    protected String role; // STUDENT, TEACHER, STAFF

    public User(String userId, String username, String email, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public abstract String getDashboardTitle();

    // Getters
    public String getUserId()   { return userId; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() { return username + " (" + role + ")"; }
}
