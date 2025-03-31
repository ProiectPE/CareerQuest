//EmployerController
package ro.unibuc.careerquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ro.unibuc.careerquest.dto.Employer;
import ro.unibuc.careerquest.service.EmployerService;
//import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.careerquest.exception.EntityNotFoundException;
//import ro.unibuc.careerquest.service.GreetingsService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ro.unibuc.careerquest.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Controller
public class EmployerController {
    @Autowired
    private EmployerService empsService;

    //list of all the emplyers
    @GetMapping("/employer")
    @ResponseBody
    public List<Employer> getAllEmployers() {
        return empsService.getAllEmployers();
    }

    //return one employer
    @GetMapping("/employer/{id}")
    @ResponseBody
    public Employer getEmployer(@PathVariable String id) throws EntityNotFoundException {
        return empsService.getEmployerById(id);
    }

    //add new employer
    @PostMapping("/employer")
    @ResponseBody
    public Employer createEmployer(@RequestBody Employer employer) {
        return empsService.saveEmployer(employer);
    }

    //update employer
    @PutMapping("/employer/{id}")
    @ResponseBody
    public Employer updateEmployer(@PathVariable String id, @RequestBody Employer employer) throws EntityNotFoundException {
        return empsService.updateEmployer(id, employer);
    }

    //delete employer
    @DeleteMapping("/employer/{id}")
    @ResponseBody
    public String deleteEmployer(@PathVariable String id) throws EntityNotFoundException {
        return empsService.deleteEmployer(id);
    }

    //some employer is paying for premium
    @PutMapping("/employer/{id}/pay")
    @ResponseBody
    public Employer payForPremium(@PathVariable String id) {
        return empsService.updatePayment(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
