package main.java.ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import main.java.ro.unibuc.hello.dto.Employer;
import main.java.ro.unibuc.hello.dto.Job;
import ro.unibuc.hello.data.InformationEntity;
import ro.unibuc.hello.data.InformationRepository;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

public class EmployerService {

     @Autowired
    private InformationRepository informationRepository;

    private final AtomicLong counter = new AtomicLong();
    private static final String helloTemplate = "Hello, %s!";
    private static final String informationTemplate = "%s : %s!";


    public Employer buildEmployerFromInfo(String name) throws EntityNotFoundException {
        InformationEntity entity = informationRepository.findByName(name);
        if (entity == null) {
            throw new EntityNotFoundException(name);
        }
        return new Employer(Long.toString(counter.incrementAndGet()), String.format(informationTemplate, entity.getName(), entity.getCompany()));
    }

    public List<Employer> getAllEmployers() {
        List<InformationEntity> entities = informationRepository.findAll();
        return entities.stream()
                .map(entity -> new Job(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }

    public Employer getEmployerById(String id) throws EntityNotFoundException {
        Optional<InformationEntity> optionalEntity = informationRepository.findById(id);
        InformationEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));
        return new Job(entity.getId(), entity.getName());
    }

    public Job saveEmployer(Employer employer) {
        InformationEntity entity = new InformationEntity();
        entity.setId(employer.getId());
        entity.setName(employer.getContent());
        informationRepository.save(entity);
        return new Employer(entity.getId(), entity.getName());
    }

    public List<Emplyer> saveAll(List<Employer> employers) {
        List<InformationEntity> entities = employers.stream()
                .map(emp -> {
                    InformationEntity entity = new InformationEntity();
                    entity.setId(emp.getId());
                    entity.setName(job.getContent());
                    return entity;
                })
                .collect(Collectors.toList());

        List<InformationEntity> savedEntities = informationRepository.saveAll(entities);

        return savedEntities.stream()
                .map(entity -> new Employer(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }

    public Job updateEmployer(String id, Employer emp) throws EntityNotFoundException {
        InformationEntity entity = informationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        entity.setName(emp.getContent());
        informationRepository.save(entity);
        return new Employer(entity.getId(), entity.getName());
    }

    public void deleteName(String id) throws EntityNotFoundException {
        InformationEntity entity = informationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        informationRepository.delete(entity);
    }

    public void deleteAllEmplyers() {
        informationRepository.deleteAll();
    }
    
}
