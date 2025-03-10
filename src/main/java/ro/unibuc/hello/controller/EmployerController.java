package main.java.ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import main.java.ro.unibuc.hello.dto.Employer;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.GreetingsService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

public class EmployerController {
    @Autowired
    private EmplyersService empsService;

    @GetMapping("/employer")
    @ResponseBody
    public List<Employer> getAllEmployers() {
        return empsService.getAllEmployers();
    }

    @GetMapping("/employer/{id}")
    @ResponseBody
    public Employer getEmployer(@PathVariable String id) throws EntityNotFoundException {
        return empsService.getEmployer(id);
    }

    @GetMapping("/info")
    @ResponseBody
    public Employer buildEmployerInfo(@RequestParam(name="title", required=false, defaultValue="Overview") String title) throws EntityNotFoundException {
        return empsService.buildEmployerFromInfo(title);
    }

    @PostMapping("/employer")
    @ResponseBody
    public Employer createEmployer(@RequestBody Employer employer) {
        return empsService.saveEmployer(employer);
    }

    @PutMapping("/employer/{id}")
    @ResponseBody
    public Employer updateEmployer(@PathVariable String id, @RequestBody Employer employer) throws EntityNotFoundException {
        return empsService.updateEmployer(id, employer);
    }

    @DeleteMapping("/employer/{id}")
    @ResponseBody
    public void deleteEmployer(@PathVariable String id) throws EntityNotFoundException {
        empsService.deleteEmployer(id);
    }
}
