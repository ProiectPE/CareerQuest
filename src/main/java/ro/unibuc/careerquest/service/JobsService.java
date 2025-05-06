
package ro.unibuc.careerquest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ro.unibuc.careerquest.data.JobEntity;
import ro.unibuc.careerquest.data.JobRepository;
import ro.unibuc.careerquest.dto.Job;
import ro.unibuc.careerquest.dto.JobContent;
import ro.unibuc.careerquest.data.ApplicationEntity;
import ro.unibuc.careerquest.dto.Application;
import ro.unibuc.careerquest.data.ApplicationRepository;
import ro.unibuc.careerquest.data.UserRepository;
import ro.unibuc.careerquest.data.CVEntity;
import ro.unibuc.careerquest.data.CVRepository;
import ro.unibuc.careerquest.data.EmployerEntity;
import ro.unibuc.careerquest.data.EmployerRepository;
import ro.unibuc.careerquest.data.UserEntity;
import ro.unibuc.careerquest.dto.CV;
import ro.unibuc.careerquest.dto.User;
import ro.unibuc.careerquest.exception.EntityNotFoundException;
import ro.unibuc.careerquest.exception.CVNotFoundException;
import ro.unibuc.careerquest.exception.UserNotFoundException;
import ro.unibuc.careerquest.exception.UsernameTakenException;
import ro.unibuc.careerquest.exception.AlreadyAppliedException;
import ro.unibuc.careerquest.data.EmployerRepository;
import ro.unibuc.careerquest.data.EmployerEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.TimeUnit;

@Component
public class JobsService {

    @Autowired
    private JobRepository jobDatabase;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private MeterRegistry metricsRegistry;

    private final AtomicLong counter = new AtomicLong();
    private final AtomicLong appCounter = new AtomicLong();

    private final AtomicLong jobViewingsCounter = new AtomicLong();
    private final AtomicLong existingJobsCounter = new AtomicLong();
    private final AtomicLong existingApplicationCounter = new AtomicLong();

    private static final String helloTemplate = "Hello, %s!";
    private static final String informationTemplate = "%s : %s!";
    private static final int FREE_POST_LIMIT = 5;


    public List<Job> getAllJobs() {
        final long start =System.currentTimeMillis();

        List<JobEntity> entities = jobDatabase.findAll();
        final long end =System.currentTimeMillis();
        metricsRegistry.timer("get_all_jobs").record(end - start, TimeUnit.MILLISECONDS);

        return entities.stream()
                .map(entity -> new Job(entity))
                .collect(Collectors.toList());
    }
  
    //get the jobs after priority
    public List<JobEntity> getAllJobsByPriority() {


        return jobDatabase.findAll().stream()
        .sorted((j1, j2) -> {
            boolean isPremium1 = (employerRepository.findByName( j1.getEmployer()) != null && employerRepository.findByName( j1.getEmployer()).isPremium());
            boolean isPremium2 = (employerRepository.findByName( j2.getEmployer()) != null && employerRepository.findByName( j2.getEmployer()).isPremium());
            return Boolean.compare(isPremium2, isPremium1); // Premium first
        })        
        .collect(Collectors.toList());
    }

    public Job getJob(String id) throws EntityNotFoundException {
        Optional<JobEntity> optionalEntity = jobDatabase.findById(id);
        JobEntity entity = optionalEntity.orElseThrow(() -> new EntityNotFoundException(id));

        metricsRegistry.counter("job_viewing", id).increment(jobViewingsCounter.incrementAndGet());

        return new Job(entity); // implemented constructor for ease
    }
    
    //return all the jobs created by an employer
    public List<Job> getJobsByEmployer(String employerId) {
        List<JobEntity> jobs = jobDatabase.findByEmployer(employerId);

        return jobs.stream()
                .map(job -> new Job(job))
                .collect(Collectors.toList());
    }

    public Job createJob(JobContent job) {
        JobEntity entity = new JobEntity(Long.toString(counter.incrementAndGet()), job); // implemented constructor for ease

        jobDatabase.save(entity);

        metricsRegistry.gauge("job_existing", existingJobsCounter.incrementAndGet());

        return new Job(entity); // implemented constructor for ease
    }

   /*
     public Job createJob(JobContent job, String employerId) {
        EmployerEntity employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new EntityNotFoundException("Employer not found"));

        // Verify if the payment for this month is done
        if (employer.getLastPaymentDate() == null || employer.getLastPaymentDate().isBefore(LocalDate.now().minusMonths(1))) {
            employer.setPremium(false); // If the payment is not done, then we don't have a premium account
            employerRepository.save(employer);
        }

        // If we are not premium, check how many free post we have
        if (!employer.isPremium()) {
            long jobCount = jobDatabase.countByEmployer(employerId);
            if (jobCount >= FREE_POST_LIMIT) {
                throw new RuntimeException("Limit exceeded. Upgrade to premium to post more jobs.");
            }
        }

        JobEntity entity = new JobEntity(Long.toString(counter.incrementAndGet()), job); // implemented constructor for ease

        jobDatabase.save(entity);
        return new Job(entity);
   
    }
*/
    
    public Job updateJob(String id, JobContent job) throws EntityNotFoundException {
        JobEntity entity = jobDatabase.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        entity.setContents(job);
        jobDatabase.save(entity);
        return new Job(entity);
    }

    public void deleteJob(String id) throws EntityNotFoundException {
        JobEntity entity = jobDatabase.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));

        metricsRegistry.gauge("job_existing", existingJobsCounter.decrementAndGet());
        
        jobDatabase.delete(entity);
    }

    public void deleteAllJobs() {
        AtomicLong existingJobsCounter = new AtomicLong(0);
        metricsRegistry.gauge("job_existing", existingJobsCounter);
        jobDatabase.deleteAll();
    }

    public List<ApplicationEntity> getApplications(String jobId) {
        List<ApplicationEntity> applications = applicationRepository.findByJobId(jobId);
        return applications;
    }

    public Application jobApply(String jobId, String cvId) throws EntityNotFoundException, CVNotFoundException, UserNotFoundException, AlreadyAppliedException {
        final long start = System.currentTimeMillis();
        
        //get job
        JobEntity job = jobDatabase.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(jobId)));

        //get cv
        Optional<CVEntity> optionalCV = cvRepository.findById(cvId);
        CVEntity cv = optionalCV.orElseThrow(() -> new CVNotFoundException(cvId));

        //verify that user has not already applied to job
        String username = cv.getUserId();
        Optional<ApplicationEntity> optionalApp = applicationRepository.findByJobIdAndUsername(jobId, username);
        optionalApp.ifPresent(app -> {throw new AlreadyAppliedException(app.getUsername());});

        //get user
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));

        //save in mongo
        ApplicationEntity app = new ApplicationEntity(Long.toString(appCounter.incrementAndGet()), jobId, cv.getUserId(), cvId);
        applicationRepository.save(app);

        //create app dto that contains all information about job cv and user
        Job jobData = new Job(job);
        CV cvData = new CV(cv.getId(), cv.getUserId(), cv.getDescription(), cv.getAchievements(), cv.getEducation(), cv.getExperience(),
                cv.getExtracurricular(), cv.getProjects(), cv.getSkills(), cv.getTools(), cv.getLanguages());
        User userData = new User(user.getUsername(), user.getDescription(), user.getFirstName(), user.getLastName(),
                user.getBirthdate(), user.getEmail(), user.getPhone());

        Application fullApp = new Application(app.getId(), jobData, userData, cvData);

        metricsRegistry.counter("applications_existing").increment(existingApplicationCounter.incrementAndGet());

        final long end = System.currentTimeMillis();

        metricsRegistry.timer("apply_time").record(end - start, TimeUnit.MILLISECONDS);

        return fullApp;
    }
}