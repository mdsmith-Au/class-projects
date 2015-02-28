/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3;

/**
 *
 * @author michael
 */
public class User {
    
    private String username, password;
    private boolean isLoggedIn;
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
    }
    
    public User() {
        username = null;
        password = null;
        isLoggedIn = false;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setLogin(boolean state) {
        isLoggedIn = state;
    }
    
    public boolean getLoginState() {
        return isLoggedIn;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
