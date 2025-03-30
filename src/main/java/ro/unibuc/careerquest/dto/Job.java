package ro.unibuc.careerquest.dto;

import ro.unibuc.careerquest.data.JobEntity;

import java.util.List;
import java.util.ArrayList;

public class Job {
    private String id;
    private String title;
    private String description;
    private String employer; // !!
    private List<String> abilities = new ArrayList<>(); // !! necessary abilities tags
    private List<String> domains = new ArrayList<>(); // !! domain tags
    private List<String> characteristics = new ArrayList<>(); // !! other characteristics tags
    private Integer salary;
    private String location;

    public Job() {
    }

    public Job(String id, String title, String description, String employer, List<String> abilities, List<String> domains, List<String> characteristics, Integer salary, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.employer = employer;
        this.abilities = (abilities != null) ? new ArrayList<>(abilities) : new ArrayList<>();
        this.domains = (domains != null) ? new ArrayList<>(domains) : new ArrayList<>();
        this.characteristics = (characteristics != null) ? new ArrayList<>(characteristics) : new ArrayList<>();
        this.salary = salary;
        this.location = location;
    }

    public Job(JobEntity e) {
        this.id = e.getId();
        setContents(e);
    }

    public Job(String id, JobEntity e) {
        this.id = id;
        setContents(e);
    }

    public void setContents(JobEntity e) {
        this.title = e.getTitle();
        this.description = e.getDescription();
        this.employer = e.getEmployer();
        this.abilities = (e.getAbilities() != null) ? new ArrayList<>(e.getAbilities()) : new ArrayList<>();
        this.domains = (e.getDomains() != null) ? new ArrayList<>(e.getDomains()) : new ArrayList<>();
        this.characteristics = (e.getCharacteristics() != null) ? new ArrayList<>(e.getCharacteristics()) : new ArrayList<>();    
        this.salary = e.getSalary();
        this.location = e.getLocation();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = new ArrayList<>(abilities);
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains =  new ArrayList<>(domains);
    }

    public List<String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<String> characteristics) {
        this.characteristics =  new ArrayList<>(characteristics);
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

