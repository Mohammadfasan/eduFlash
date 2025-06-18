package lk.cmb.eduflash;

/**
 * This class represents a User.
 * The field names (username, email) must match the keys in the Firebase Realtime Database
 * for automatic data mapping with DataSnapshot.getValue(User.class).
 */
public class User {
    private String username;
    private String email;

    // A default, no-argument constructor is required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}