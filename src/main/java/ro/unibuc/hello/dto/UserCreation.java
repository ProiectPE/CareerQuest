package ro.unibuc.hello.dto;

public class UserCreation {
    
    private String username;
    private String password;
    private String email;

    public UserCreation() {}

    public UserCreation(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    } 

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setEmail(String email) {this.email = email;}
}
