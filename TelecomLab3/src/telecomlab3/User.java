package telecomlab3;

/**
 * Represents a user and their known state. Note all methods are synchronized,
 * so they can only be accessed by one thread at at a time.
 */
public class User {
    private String username, password;
    private boolean isLoggedIn;

    /**
     * Creates a new User with given password and username. They are set to not
     * be currently logged in.
     *
     * @param username The username
     * @param password The password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
    }

    /**
     * Creates a new user with null username and password. They are set to not
     * be currently logged in.
     */
    public User() {
        username = null;
        password = null;
        isLoggedIn = false;
    }

    /**
     * Getter for the username.
     *
     * @return The username.
     */
    public synchronized String getUsername() {
        return username;
    }

    /**
     * Getter for the password.
     *
     * @return The password.
     */
    public synchronized String getPassword() {
        return password;
    }

    /**
     * Setter for the user's log in state.
     *
     * @param state The user's log in state - true if logged in.
     */
    public synchronized void setLogin(boolean state) {
        isLoggedIn = state;
    }

    /**
     * Getter for the user's log in state.
     *
     * @return The user's log in state - true if logged in.
     */
    public synchronized boolean getLoginState() {
        return isLoggedIn;
    }

    /**
     * Setter for the username.
     *
     * @param username The username
     */
    public synchronized void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setter for the password.
     *
     * @param password The password.
     */
    public synchronized void setPassword(String password) {
        this.password = password;
    }
}
