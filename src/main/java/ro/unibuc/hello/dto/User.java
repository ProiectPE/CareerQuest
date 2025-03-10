package ro.unibuc.hello.dto;

import java.time.Period;
import java.time.LocalDate;

public class User {

    private String username;

    private String description;

    private String firstName;
    private String lastName;
    private String fullName;

    private LocalDate birthdate;
    private int age;
    
    private String email;
    private String phone;

    private String CV;

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String description, String firstName, String lastName, LocalDate birthdate, String email, String phone) {
        this.username = username;
        this.description = description;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + lastName;
        this.birthdate = birthdate;
        this.email = email;
        this.phone = phone;
    }

    public String getUsername() {return username;}
    
    public String getName() {return fullName;}
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
    public int getAge() {return age;}
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        if(birthdate != null) {
            LocalDate currentDate = LocalDate.now();
            this.age = Period.between(birthdate, currentDate).getYears();
        }
        else
            this.age = 0;
    } 

    public String getEmail() {return email;}
    public String getPhone() {return phone;}
    public void setEmail(String email) {this.email = email;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getCV() {return CV;}
    public void setCV(String CV) {this.CV = CV;}
}
