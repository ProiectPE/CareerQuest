
package ro.unibuc.careerquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ro.unibuc.careerquest.dto.Job;
import ro.unibuc.careerquest.dto.JobContent;
import ro.unibuc.careerquest.data.JobEntity;
import ro.unibuc.careerquest.dto.Application;
import ro.unibuc.careerquest.data.ApplicationEntity;
import ro.unibuc.careerquest.exception.EntityNotFoundException;
import ro.unibuc.careerquest.service.JobsService;

import ro.unibuc.careerquest.exception.CVNotFoundException;
import ro.unibuc.careerquest.exception.UserNotFoundException;
import ro.unibuc.careerquest.exception.AlreadyAppliedException;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class JobsController {
    @Autowired
    private JobsService jobsService;

    @GetMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "hello";
    }

    @GetMapping("/job")
    @ResponseBody
    public List<Job> getAllJobs() {
        return jobsService.getAllJobs();
    }
  
     @GetMapping("/job/priority")
      @ResponseBody
      public List<JobEntity> getAllJobsByPriority() {
          return jobsService.getAllJobsByPriority();
      }

    @GetMapping("/job/{id}")
    @ResponseBody
    public Job getJob(@PathVariable String id) throws EntityNotFoundException {
        return jobsService.getJob(id);
    }
  
  // list of all the jobs posted by an employer
    @GetMapping("/jobs/employer/{employerId}")
    @ResponseBody
    public List<JobEntity> getJobsByEmployer(@PathVariable String employerId) {
        return jobsService.getJobsByEmployer(employerId);
    }
    
    @PostMapping("/job")
    @ResponseBody
    public Job createJob(@RequestBody JobContent job,String employerId) {
        return jobsService.createJob(job);
    }

    @PutMapping("/job/{id}")
    @ResponseBody
    public Job updateJob(@PathVariable String id, @RequestBody JobContent job) throws EntityNotFoundException {
        return jobsService.updateJob(id, job);
    }

    // add tags!!!!!!!!!!!!!!!!!!!!!!!

    @DeleteMapping("/job/{id}")
    @ResponseBody
    public void deleteJob(@PathVariable String id) throws EntityNotFoundException {
        jobsService.deleteJob(id);
    }


    @GetMapping("/job-app/{id}")
    @ResponseBody
    public List<ApplicationEntity> getApplications(@PathVariable String id) {
        return jobsService.getApplications(id);
    }

    @PostMapping("/job-app/{id}")
    @ResponseBody
    public Application jobApply(@PathVariable String id, @RequestParam String cvId) throws EntityNotFoundException, CVNotFoundException, UserNotFoundException, AlreadyAppliedException {
        return jobsService.jobApply(id, cvId);
    }
}
