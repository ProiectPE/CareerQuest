package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.GreetingsService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class GreetingsController {

    @Autowired
    private GreetingsService greetingsService;

    // @GetMapping("/user")
    // @ResponseBody
    // public List<User> getAllUsers() {
    //     return greetingsService.getAllUsers();
    // }

    // @GetMapping("/user/{id}")
    // @ResponseBody
    // public User getUser(@PathVariable String id) throws EntityNotFoundException {
    //     return greetingsService.getUser(id);
    // }

    // @PostMapping("/user")
    // @ResponseBody
    // public User createUser(@RequestBody User user) {
    //     return greetingsService.saveUser(user);
    // }

    // @PutMapping("/user/{id}")
    // @ResponseBody
    // public User updateUser(@PathVariable String id, @RequestBody User user) throws EntityNotFoundException {
    //     return greetingsService.updateUser(id, user);
    // }

    // @DeleteMapping("/user/{id}")
    // @ResponseBody
    // public void deleteUser(@PathVariable String id) throws EntityNotFoundException {
    //     greetingsService.deleteUser(id);
    // }

    // @GetMapping("/cv")
    // @ResponseBody
    // public List<CV> getAllCVs() {
    //     return greetingsService.getAllCVs();
    // }

    // @GetMapping("/cv/{id}")
    // @ResponseBody
    // public CV getCV(@PathVariable String id) throws EntityNotFoundException {
    //     return greetingsService.getCV(id);
    // }

    // @PostMapping("/cv")
    // @ResponseBody
    // public CV createCV(@RequestBody CV cv) {
    //     return greetingsService.saveCV(cv);
    // }

    // @PutMapping("/cv/{id}")
    // @ResponseBody
    // public CV updateCV(@PathVariable String id, @RequestBody CV cv) throws EntityNotFoundException {
    //     return greetingsService.updateCV(id, cv);
    // }

    // @DeleteMapping("/cv/{id}")
    // @ResponseBody
    // public void deleteCV(@PathVariable String id) throws EntityNotFoundException {
    //     greetingsService.deleteCV(id);
    // }

    

    // @GetMapping("/employer")
    // @ResponseBody
    // public List<Employer> getAllEmployers() {
    //     return greetingsService.getAllEmployers();
    // }

    // @GetMapping("/employer/{id}")
    // @ResponseBody
    // public Employer getEmployer(@PathVariable String id) throws EntityNotFoundException {
    //     return greetingsService.getEmployer(id);
    // }

    // @PostMapping("/employer")
    // @ResponseBody
    // public Employer createEmployer(@RequestBody Employer employer) {
    //     return greetingsService.saveEmployer(employer);
    // }

    // @PutMapping("/employer/{id}")
    // @ResponseBody
    // public Employer updateEmployer(@PathVariable String id, @RequestBody Employer employer) throws EntityNotFoundException {
    //     return greetingsService.updateEmployer(id, employer);
    // }

    // @DeleteMapping("/employer/{id}")
    // @ResponseBody
    // public void deleteEmployer(@PathVariable String id) throws EntityNotFoundException {
    //     greetingsService.deleteEmployer(id);
    // }

    // @GetMapping("/tags")
    // @ResponseBody
    // public List<Tag> getAllTags() {
    //     return greetingsService.getAllTags();
    // }
    
    // @GetMapping("/recommendjobs")
    // @ResponseBody
    // public List<Job> getRecommendedJobs(@RequestParam(name="id", required=true) String id) {
    //     return greetingsService.getRecommendedJobs(id);
    // }

    // @GetMapping("/bestcandidates")
    // @ResponseBody
    // public List<User> getBestCandidates(@RequestParam(name="id", required=true) String id) {
    //     return greetingsService.getBestCandidates(id);
    // }

    // a /*
    @GetMapping("/hello-world")
    @ResponseBody
    public Greeting sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
        return greetingsService.hello(name);
    }

    @GetMapping("/info")
    @ResponseBody
    public Greeting info(@RequestParam(name="title", required=false, defaultValue="Overview") String title) throws EntityNotFoundException {
        return greetingsService.buildGreetingFromInfo(title);
    }

    @GetMapping("/greetings")
    @ResponseBody
    public List<Greeting> getAllGreetings() {
        return greetingsService.getAllGreetings();
    }


    @PostMapping("/greetings")
    @ResponseBody
    public Greeting createGreeting(@RequestBody Greeting greeting) {
        return greetingsService.saveGreeting(greeting);
    }

    @PutMapping("/greetings/{id}")
    @ResponseBody
    public Greeting updateGreeting(@PathVariable String id, @RequestBody Greeting greeting) throws EntityNotFoundException {
        return greetingsService.updateGreeting(id, greeting);
    }

    @DeleteMapping("/greetings/{id}")
    @ResponseBody
    public void deleteGreeting(@PathVariable String id) throws EntityNotFoundException {
        greetingsService.deleteGreeting(id);
    }
    // */
}