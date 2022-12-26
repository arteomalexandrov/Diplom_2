package entity.user;

public class Identity {
    private String email;
    private String password;
    private User user;

    public Identity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Identity(User user) {
        this(user.getEmail(), user.getPassword());
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
