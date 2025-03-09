package main.java.ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.unibuc.hello.data.InformationEntity;
import ro.unibuc.hello.data.InformationRepository;
import ro.unibuc.hello.dto.Job;
import ro.unibuc.hello.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class JobsService {

    @Autowired
    private InformationRepository informationRepository;

    private final AtomicLong counter = new AtomicLong();
    private static final String helloTemplate = "Hello, %s!";
    private static final String informationTemplate = "%s : %s!";

    // public Job hello(String name) {
    //     return new Job(Long.toString(counter.incrementAndGet()), String.format(helloTemplate, name));
    // }

    public Job buildJobFromInfo(String title) throws EntityNotFoundException {
        InformationEntity entity = informationRepository.findByTitle(title);
        if (entity == null) {
            throw new EntityNotFoundException(title);
        }
        return new Job(Long.toString(counter.incrementAndGet()), String.format(informationTemplate, entity.getTitle(), entity.getDescription()));
    }

    public List<Job> getAllJobs() {
        List<InformationEntity> entities = informationRepository.findAll();
        return entities.stream()
                .map(entity -> new Job(entity.getId(), entity.getTitle()))
                .collect(Collectors.toList());
    }

    public Job getJobById(String id) throws EntityNotFoundException {
        Optional<InformationEntity> optionalEntity = informationRepository.findById(id);
        InformationEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));
        return new Job(entity.getId(), entity.getTitle());
    }

    public Job saveJob(Job job) {
        InformationEntity entity = new InformationEntity();
        entity.setId(job.getId());
        entity.setTitle(job.getContent());
        informationRepository.save(entity);
        return new Job(entity.getId(), entity.getTitle());
    }

    public List<Job> saveAll(List<Job> jobs) {
        List<InformationEntity> entities = jobs.stream()
                .map(job -> {
                    InformationEntity entity = new InformationEntity();
                    entity.setId(job.getId());
                    entity.setTitle(job.getContent());
                    return entity;
                })
                .collect(Collectors.toList());

        List<InformationEntity> savedEntities = informationRepository.saveAll(entities);

        return savedEntities.stream()
                .map(entity -> new Job(entity.getId(), entity.getTitle()))
                .collect(Collectors.toList());
    }

    public Job updateJob(String id, Job job) throws EntityNotFoundException {
        InformationEntity entity = informationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        entity.setTitle(job.getContent());
        informationRepository.save(entity);
        return new Job(entity.getId(), entity.getTitle());
    }

    public void deleteJob(String id) throws EntityNotFoundException {
        InformationEntity entity = informationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        informationRepository.delete(entity);
    }

    public void deleteAllJobs() {
        informationRepository.deleteAll();
    }
}
