package ro.unibuc.careerquest.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import ro.unibuc.careerquest.data.ApplicationEntity;
import ro.unibuc.careerquest.data.ApplicationRepository;
import ro.unibuc.careerquest.data.CVComponent;
import ro.unibuc.careerquest.data.CVEntity;
import ro.unibuc.careerquest.data.CVRepository;
import ro.unibuc.careerquest.data.JobEntity;
import ro.unibuc.careerquest.data.JobRepository;
import ro.unibuc.careerquest.data.UserEntity;
import ro.unibuc.careerquest.data.UserRepository;
import ro.unibuc.careerquest.dto.Application;
import ro.unibuc.careerquest.dto.Job;
import ro.unibuc.careerquest.dto.JobContent;
import ro.unibuc.careerquest.exception.AlreadyAppliedException;
import ro.unibuc.careerquest.exception.EntityNotFoundException;
import ro.unibuc.careerquest.exception.JobNotFoundException;

@ExtendWith(SpringExtension.class)
public class JobsServiceTest {
    @Mock
    private JobRepository jobDatabase;

    @Mock 
    private ApplicationRepository applicationRepository;

    @Mock 
    private UserRepository userRepository;

    @Mock 
    private CVRepository cvRepository;

    @Mock
    private MeterRegistry metricsRegistry;

    @InjectMocks
    private JobsService jobsService = new JobsService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    JobEntity job_entity1 = new JobEntity("1", "Senior Software Engineer", "description", 
                                "Adobe", Arrays.asList("Java", "C"), Arrays.asList("SWE"),
                                Arrays.asList("Leadership", "Problem Solving"), 5000, "Romania");
        
    JobEntity job_entity2 = new JobEntity("2", "QA Enginner", "description", 
    "Amazon", Arrays.asList("DevOps", "C", "Agile"), Arrays.asList("Testing"),
    Arrays.asList("Leadership", "Problem Solving"), 10000, "Romania");

    @Test
    public void test_getAllJobs() {
        List<JobEntity> jobs = Arrays.asList(job_entity1, job_entity2);

        when(jobDatabase.findAll()).thenReturn(jobs);

        List<Job> jobs_new = jobsService.getAllJobs();

        assertEquals(2, jobs_new.size());
        assertEquals("1", jobs_new.get(0).getId());
        assertEquals("2", jobs_new.get(1).getId());
        assertEquals("Senior Software Engineer", jobs_new.get(0).getTitle());
    }

    @Test
    public void test_getJobById() {
        String jobId = "1";
        when(jobDatabase.findById(jobId)).thenReturn(Optional.of(job_entity1));

        Job job = jobsService.getJob(jobId);

        assertEquals("1", job.getId());
        assertEquals("Senior Software Engineer", job.getTitle());
        assertEquals("Adobe", job.getEmployer());
    }

    @Test
    public void test_deleteExistingJob() {
        String jobId = "1";
        when(jobDatabase.findById(jobId)).thenReturn(Optional.of(job_entity1));

        jobsService.deleteJob(jobId);

        verify(jobDatabase, times(1)).delete(job_entity1);
    }

    @Test
    public void test_deleteNonExistingJob() {
        String jobId = "3";
        when(jobDatabase.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> jobsService.deleteJob(jobId));
    }

    @Test
    public void test_updateJob() {
        String jobId = "1";
        JobContent jobContent = new JobContent("New Title", "New Description", "New Employer",
                Arrays.asList("New Skill 1", "New Skill 2"), Arrays.asList("New Tool 1", "New Tool 2"), Arrays.asList("New Requirement 1", "New Requirement 2"), 8000, "New Location");
        JobEntity new_entity = new JobEntity("1", jobContent);

        when(jobDatabase.findById(jobId)).thenReturn(Optional.of(job_entity1));
        when(jobDatabase.save(any(JobEntity.class))).thenReturn(new_entity);

        Job updatedJob = jobsService.updateJob(jobId, jobContent);

        assertEquals("1", updatedJob.getId());
        assertEquals("New Title", updatedJob.getTitle());
        assertEquals("New Description", updatedJob.getDescription());
        assertEquals("New Employer", updatedJob.getEmployer());
        assertEquals("New Skill 1", updatedJob.getAbilities().get(0));
        assertEquals("New Skill 2", updatedJob.getAbilities().get(1));
        assertEquals("New Tool 1", updatedJob.getDomains().get(0));
        assertEquals("New Tool 2", updatedJob.getDomains().get(1));
        assertEquals("New Requirement 1", updatedJob.getCharacteristics().get(0));
        assertEquals("New Requirement 2", updatedJob.getCharacteristics().get(1));
        assertEquals(8000, updatedJob.getSalary());
        assertEquals("New Location", updatedJob.getLocation());
    }

    @Test
    public void test_createJob() {

        JobContent jobContent = new JobContent("New Title", "New Description", "New Employer", 
                Arrays.asList("New Skill 1", "New Skill 2"), Arrays.asList("New Tool 1", "New Tool 2"), 
                Arrays.asList("New Requirement 1", "New Requirement 2"), 8000, "New Location");
        when(jobDatabase.save(any(JobEntity.class))).thenReturn(job_entity1);

        Job job = jobsService.createJob(jobContent);

        assertEquals("1", job.getId());
        assertEquals("New Title", job.getTitle());

    }

    @Test
    public void test_jobApply() {

        String jobId = "1";
        String cvId = "1";
        String username = "bagsylina";

        CVEntity cv_entity = new CVEntity("1", "bagsylina", "description", "achievements");
        UserEntity user_entity = new UserEntity("bagsylina", "password", "email");
        
        cv_entity.addEducation(new CVComponent(LocalDate.of(2022,10,1), "education", "Bachelor of CS", "Unibuc"));
        cv_entity.addSkill("Java");
        cv_entity.addSkill("C");
        cv_entity.addTool("SWE");
    
        when(jobDatabase.findById(jobId)).thenReturn(Optional.of(job_entity1));
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv_entity));
        when(userRepository.findById(username)).thenReturn(Optional.of(user_entity));
        when(applicationRepository.findByJobIdAndUsername(jobId, username)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(ApplicationEntity.class))).thenReturn(new ApplicationEntity("1", jobId, username, cvId));

        Counter mockCounter = mock(Counter.class);
        when(metricsRegistry.counter("nr_job_applications", "job", jobId)).thenReturn(mockCounter);

        Application app = jobsService.jobApply(jobId, cvId);
        metricsRegistry.counter("nr_job_applications", "job", jobId).increment();

        assertEquals("1", app.getId());
        assertEquals("1", app.getJob().getId());
        assertEquals("1", app.getCV().getId());
        assertEquals("bagsylina", app.getUser().getUsername());
    }

    @Test
    public void test_jobApplyError() {

        String jobId = "1";
        String cvId = "1";
        String username = "bagsylina";

        CVEntity cv_entity = new CVEntity("1", "bagsylina", "description", "achievements");
        UserEntity user_entity = new UserEntity("bagsylina", "password", "email");
        
        cv_entity.addEducation(new CVComponent(LocalDate.of(2022,10,1), "education", "Bachelor of CS", "Unibuc"));
        cv_entity.addSkill("Java");
        cv_entity.addSkill("C");
        cv_entity.addTool("SWE");
    
        when(jobDatabase.findById(jobId)).thenReturn(Optional.of(job_entity1));
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv_entity));
        when(userRepository.findById(username)).thenReturn(Optional.of(user_entity));
        when(applicationRepository.findByJobIdAndUsername(jobId, username)).thenReturn(Optional.of(new ApplicationEntity("1", jobId, username, cvId)));
        when(applicationRepository.save(any(ApplicationEntity.class))).thenReturn(new ApplicationEntity("1", jobId, username, cvId));

        assertThrows(AlreadyAppliedException.class, () -> jobsService.jobApply(jobId, cvId));
    }
}
