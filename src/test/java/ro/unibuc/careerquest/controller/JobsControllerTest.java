package ro.unibuc.careerquest.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.codecs.jsr310.LocalDateCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.cucumber.java.Before;
import ro.unibuc.careerquest.dto.Job;
import ro.unibuc.careerquest.dto.JobContent;
import ro.unibuc.careerquest.dto.UserCreation;
import ro.unibuc.careerquest.exception.InvalidEmailException;
import ro.unibuc.careerquest.service.JobsService;




public class JobsControllerTest {
    @Mock
    private JobsService jobsService;

    @InjectMocks
    private JobsController jobsController;

    private MockMvc mockMvc;


    Job job_sample = new Job("1", "Senior Software Developer", "description", 
                                                "Adobe", Arrays.asList("Java"), Arrays.asList("SWE"),
                                                Arrays.asList("Leadership"), 5000, "Romania");

    @BeforeEach
    public void setUp() { // we might leave this method empty!!
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jobsController).build();
    }

    @Test
    public void test_getAllJobs() throws Exception {
        // public Job(String id, String title, String description, String employer, List<String> abilities, List<String> domains, List<String> characteristics, Integer salary, String location) {
        List<Job> jobs = Arrays.asList( new Job("1", "Senior Software Developer", "description", 
                                                "Adobe", Arrays.asList("Java", "C"), Arrays.asList("SWE"),
                                                Arrays.asList("Leadership", "Problem Solving"), 5000, "Romania"),
                new Job("2", "QA Engineer", "description2", "Amazon", 
                        Arrays.asList("DevOps", "Agile"), Arrays.asList("Testing", "Development", "Delivery"), 
                        Arrays.asList("Problem Solving"), 10000, "Romania"));

        when(jobsService.getAllJobs()).thenReturn(jobs);

        mockMvc.perform(get("/job"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("Senior Software Developer"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].title").value("QA Engineer"));
    }

    @Test
    public void test_getJob() throws Exception {
        String id = "1";
        // public Job(String id, String title, String description, String employer, List<String> abilities, List<String> domains, List<String> characteristics, Integer salary, String location) {
        Job job = new Job("1", "Senior Software Developer", "description", 
                                                "Adobe", Arrays.asList("Java", "C"), Arrays.asList("SWE"),
                                                Arrays.asList("Leadership", "Problem Solving"), 5000, "Romania");

        when(jobsService.getJob(id)).thenReturn(job);

        //mockMvc.perform(get("/job/{id}", id)).andDo(print());
        mockMvc.perform(get("/job/{id}", id))   
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Senior Software Developer"))
                .andExpect(jsonPath("$.salary").value("5000"))
                .andExpect(jsonPath("$.abilities[0]").value("Java"));
    }

    @Test
    public void test_createJob() throws Exception {
        

        when(jobsService.createJob(any(JobContent.class))).thenReturn(job_sample);

        mockMvc.perform(post("/job") // it's quite irrelevant what content we give it, we just have to make sure it's something valid
            .content("{\"title\": \"Senior Software Engineer\", \"description\": \"description\", \"employer\": \"Adobe\", \"abilities\": [\"Java\"], \"domains\": [\"SWE\"], \"characteristics\": [\"Leadership\"], \"salary\": 5000, \"location\": \"Romania\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.title").value("Senior Software Developer"))
            .andExpect(jsonPath("$.salary").value(5000));
    }

    @Test
    public void test_updateJob() throws Exception {

        String id = "1";

        Job updatedJob = new Job("1", "Senior Software Engineer", "description", 
                                "Adobe", Arrays.asList("Java", "C"), Arrays.asList("SWE"),
                                Arrays.asList("Leadership", "Problem Solving"), 5000, "Romania");

        // check what happens when id doesn't match
        when(jobsService.updateJob(eq(id), any(JobContent.class))).thenReturn(updatedJob);

        mockMvc.perform(put("/job/{id}", id)
            .content("{\"title\": \"Senior Software Engineer\", \"description\": \"description\", \"employer\": \"Adobe\", \"abilities\": [\"Java\", \"C\"], \"domains\": [\"SWE\"], \"characteristics\": [\"Leadership\"], \"salary\": 5000, \"location\": \"Romania\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Senior Software Engineer"))
            .andExpect(jsonPath("$.abilities[1]").value("C"));

    }

    @Test
    void test_deleteJob() throws Exception {
        String id = "1";
        
        when(jobsService.createJob(any(JobContent.class))).thenReturn(job_sample);

        //first create user
        mockMvc.perform(post("/job")
            .content("{\"title\": \"Senior Software Engineer\", \"description\": \"description\", \"employer\": \"Adobe\", \"abilities\": [\"Java\"], \"domains\": [\"SWE\"], \"characteristics\": [\"Leadership\"], \"salary\": 5000, \"location\": \"Romania\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        
        //then delete the user
        mockMvc.perform(delete("/job/{id}", id))
            .andExpect(status().isOk());

        //check user is deleted
        verify(jobsService, times(1)).deleteJob(id);

        mockMvc.perform(get("/job"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isEmpty());
    }
}
