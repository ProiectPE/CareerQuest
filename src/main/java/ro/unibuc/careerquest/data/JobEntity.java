package ro.unibuc.careerquest.data;

import org.springframework.data.annotation.Id;

import ro.unibuc.careerquest.dto.Job;
import ro.unibuc.careerquest.dto.JobContent;

import java.util.List;
import java.util.ArrayList;

public class JobEntity {

    @Id
    private String id;

    private String title;
    private String description;
    private String employer; // !!
    private List<String> abilities = new ArrayList<>(); // !! necessary abilities tags
    private List<String> domains = new ArrayList<>(); // !! domain tags
    private List<String> characteristics = new ArrayList<>(); // !! other characteristics tags
    private Integer salary;
    private String location;

    public JobEntity() {}

    public JobEntity(String id, JobContent job) {
        this.id = id;
        setContents(job);
        // this.title = job.getTitle();
        // this.description = job.getDescription();
        // this.employer = job.getEmployer();
        // this.abilities = job.getAbilities(); // here a copy??
        // this.domains = job.getDomains();
        // this.characteristics = job.getCharacteristics();
        // this.salary = job.getSalary();
        // this.location = job.getLocation();
    }

    public JobEntity(String id, String title, String description, String employer, List<String> abilities, List<String> domains, List<String> characteristics, Integer salary, String location) {
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

    // public JobEntity(Job job) {
    //     this.id = job.getId();
    //     setContents(job);
    // }

    // public JobEntity(String id, Job job) {
    //     this.id = id;
    //     setContents(job);
    // }

    public void setContents(JobContent job) {
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.employer = job.getEmployer();
        this.abilities = new ArrayList<>(job.getAbilities());
        this.domains = new ArrayList<>(job.getDomains());
        this.characteristics = new ArrayList<>(job.getCharacteristics());
        this.salary = job.getSalary();
        this.location = job.getLocation();
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
        this.domains = new ArrayList<>(domains);
    }

    public List<String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<String> characteristics) {
        this.characteristics = new ArrayList<>(characteristics);
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

    @Override
    public String toString() {
        return String.format(
                "Information[id='%s', title='%s', description='%s']",
                id, title, description);
    }
}
