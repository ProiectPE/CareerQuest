package ro.unibuc.hello.data;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;

public class UserEntity {
    
    @Id
    private String username;
    private String password;

    private String description;

    private String fullName;
    private String firstName;
    private String lastName;

    private LocalDate birthdate;
    
    private String email;
    private String phone;

    private String CV;

    public UserEntity() {}

    public UserEntity(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    } 

    public UserEntity(String username, String password, String description, String firstName, String lastName, LocalDate birthdate, String email, String phone) {
        this.username = username;
        this.password = password;
        this.description = description;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + lastName;
        this.birthdate = birthdate;
        this.email = email;
        this.phone = phone;
    }

    public String getUsername() {return username;}

    public void setPassword(String password) {this.password = password;} 
    public boolean validatePassword(String password) {
        if(this.password.equals(password))
            return true;
        return false;
    } 

    public String getFullName() {return fullName;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.fullName = firstName + lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
        this.fullName = firstName + lastName;
    }

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public LocalDate getBirthdate() {return birthdate;}
    public void setBirthdate(LocalDate birthdate) {this.birthdate = birthdate;} 

    public String getEmail() {return email;}
    public String getPhone() {return phone;}
    public void setEmail(String email) {this.email = email;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getCV() {return CV;}
    public void setCV(String CV) {this.CV = CV;}
}
