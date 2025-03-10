package main.java.ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ro.unibuc.hello.dto.Job;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.JobsService;
import ro.unibuc.hello.service.JobsService;

import org.springframework.web.bind.annotation.*;
import java.util.List;


public class JobsController {
    @Autowired
    private JobsService jobsService;

    @GetMapping("/job")
    @ResponseBody
    public List<Job> getAllJobs() {
        return jobsService.getAllJobs();
    }

    @GetMapping("/job/{id}")
    @ResponseBody
    public Job getJob(@PathVariable String id) throws EntityNotFoundException {
        return jobsService.getJob(id);
    }

    @GetMapping("/info")
    @ResponseBody
    public Job buildJobInfo(@RequestParam(name="title", required=false, defaultValue="Overview") String title) throws EntityNotFoundException {
        return jobsService.buildJobFromInfo(title);
    }

    @PostMapping("/job")
    @ResponseBody
    public Job createJob(@RequestBody Job job) {
        return jobsService.saveJob(job);
    }

    @PutMapping("/job/{id}")
    @ResponseBody
    public Job updateJob(@PathVariable String id, @RequestBody Job job) throws EntityNotFoundException {
        return jobsService.updateJob(id, job);
    }

    @DeleteMapping("/job/{id}")
    @ResponseBody
    public void deleteJob(@PathVariable String id) throws EntityNotFoundException {
        jobsService.deleteJob(id);
    }
}
