package ro.unibuc.careerquest.dto;

import java.time.LocalDate;

public class Employer {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private boolean premium;
    private LocalDate lastPaymentDate;
    
    

    public Employer() {}

    public Employer(String id, String name, String email, String phone, String company, LocalDate lastPaymentDate, boolean premium) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.lastPaymentDate = lastPaymentDate;
        this.premium = premium;
    }
    
    public Employer(String id, String name, String email, String phone, String company) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.company = company;
    }

    public Employer(String id, String name) {
        this.id = id;
        this.name = name;
        
    }


    public LocalDate getLastPaymentDate()
    {
        return lastPaymentDate;
    }
    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}


