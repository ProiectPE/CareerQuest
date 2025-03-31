//EmployerService
package ro.unibuc.careerquest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ro.unibuc.careerquest.dto.Employer;
import ro.unibuc.careerquest.data.EmployerEntity;
import ro.unibuc.careerquest.data.EmployerRepository;

import ro.unibuc.careerquest.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class EmployerService {

    @Autowired
    private EmployerRepository employerRepository;

    private final AtomicLong counter = new AtomicLong();
    private static final String helloTemplate = "Hello, %s!";
    private static final String informationTemplate = "%s : %s!";

    //list of all the employers
    public List<Employer> getAllEmployers() {
        List<EmployerEntity> entities = employerRepository.findAll();
        return entities.stream()
                .map(entity -> new Employer(entity.getId(), entity.getName(),entity.getEmail(),entity.getPhone(), entity.getCompany(),entity.getLastPaymentDate(),entity.isPremium()))
                .collect(Collectors.toList());
    }

    // public Employer getEmployerById(String id) throws EntityNotFoundException {
    //     Optional<EmployerEntity> optionalEntity = employerRepository.findById(id);
    //     EmployerEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));
    //     return new Employer(entity.getId(), entity.getName());
    // }

    //search if employer by id exist
    public Employer getEmployerById(String employerId) {
        // Căutăm employer-ul după ID
        EmployerEntity entity = employerRepository.findById(employerId)
                .orElseThrow(() -> new EntityNotFoundException("Employer not found"));
    
        // Returnăm un obiect Employer pe baza entității găsite
        return new Employer(entity.getId(), entity.getName(), entity.getEmail(), entity.getPhone(), entity.getCompany(),entity.getLastPaymentDate(),entity.isPremium());
    }
    

    public Employer saveEmployer(Employer employer) {
        EmployerEntity entity = new EmployerEntity();

        entity.setId(Long.toString(counter.incrementAndGet()));
        entity.setName(employer.getName());
        entity.setCompany(employer.getCompany());
        entity.setEmail(employer.getEmail());
        entity.setPhone(employer.getPhone());
        entity.setLastPaymentDate(employer.getLastPaymentDate());
        entity.setPremium(employer.isPremium());
        employerRepository.save(entity);
        return new Employer(entity.getId(), entity.getName(),entity.getEmail(),entity.getPhone(),entity.getCompany(),entity.getLastPaymentDate(),entity.isPremium());
    }
/*
    public List<Employer> saveAll(List<Employer> employers) {
        List<EmployerEntity> entities = employers.stream()
                .map(emp -> {
                    EmployerEntity entity = new EmployerEntity();
                    entity.setId(emp.getId());
                    entity.setName(emp.getName());
                    return entity;
                })
                .collect(Collectors.toList());

        List<EmployerEntity> savedEntities = employerRepository.saveAll(entities);

        return savedEntities.stream()
                .map(entity -> new Employer(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }
*/
    //modifying an emplyer
    public Employer updateEmployer(String id, Employer emp) throws EntityNotFoundException {
        EmployerEntity entity = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        if(emp.getName()!= null)
            entity.setName(emp.getName());
        if(emp.getCompany()!=null)
            entity.setCompany(emp.getCompany());
        if(emp.getPhone()!=null)
            entity.setPhone(emp.getPhone());
        if(emp.getEmail()!=null)
            entity.setEmail(emp.getEmail());
        employerRepository.save(entity);
        return new Employer(entity.getId(), entity.getName(),entity.getEmail(),entity.getPhone(),entity.getCompany());
    }
    
    public String deleteEmployer(String id) throws EntityNotFoundException {
        EmployerEntity entity = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        employerRepository.delete(entity);
        return "Employer by id=" + id + " was deleted.";
    }

    public void deleteAllEmplyers() {
        employerRepository.deleteAll();

    }
    
    public Employer updatePayment( String id) throws EntityNotFoundException {
        EmployerEntity entity = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        entity.setLastPaymentDate(LocalDate.now());
        entity.setPremium(true);
        employerRepository.save(entity);
        return new Employer(entity.getId(), entity.getName(),entity.getEmail(),entity.getPhone(),entity.getCompany(),entity.getLastPaymentDate(),entity.isPremium());
  
    }
    

}
