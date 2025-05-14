package ro.unibuc.careerquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.careerquest.dto.Employer;
import ro.unibuc.careerquest.dto.Job;
import ro.unibuc.careerquest.dto.JobContent;
import ro.unibuc.careerquest.service.EmployerService;
import ro.unibuc.careerquest.service.JobsService;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.service.GreetingsService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class JobsControllerIntegrationTesting {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.20")
            .withExposedPorts(27017)
            .withSharding();

    @BeforeAll
    public static void setUp() {
        mongoDBContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        //final String MONGO_URL = "mongodb://root:example@localhost:";
        final String MONGO_URL = "mongodb://localhost:";
        final String PORT = String.valueOf(mongoDBContainer.getMappedPort(27017));

        registry.add("mongodb.connection.url", () -> MONGO_URL + PORT);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobsService jobsService;

    @Autowired
    private EmployerService employerService;

    @BeforeEach
    public void cleanUpAndAddTestData() {
        jobsService.deleteAllJobs();

        JobContent job_content1 = new JobContent("Senior Software Developer", "description",
                                                "Adobe", Arrays.asList("Java", "C"), Arrays.asList("SWE"),
                                                Arrays.asList("Leadership", "Problem Solving"), 5000, "Romania");
        JobContent job_content2 = new JobContent("QA Engineer", "description2", "Amazon",
                        Arrays.asList("DevOps", "Agile"), Arrays.asList("Testing", "Development", "Delivery"),
                        Arrays.asList("Problem Solving"), 10000, "Romania");

        // private String name;
    // private String email;
    // private String phone;
    // private String company;
    // private LocalDate lastPaymentDate;
    // private boolean premium;
        Employer employer1 = new Employer("1", "John", "john@gmail.com", "123456", "Adobe", LocalDate.now(), true);
        Employer employer2 = new Employer("2", "Diana", "diana@gmail.com", "123456", "Amazon", LocalDate.now(), true);

        employerService.saveEmployer(employer1);
        employerService.saveEmployer(employer2);

        jobsService.createJob(job_content1);
        jobsService.createJob(job_content2);


    }

    @Test
    public void testGetAllJobs() throws Exception {
        mockMvc.perform(get("/job"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Senior Software Developer"))
            .andExpect(jsonPath("$[1].salary").value(10000));
    }

    @Test
    public void testCreateJob() throws Exception {
        JobContent job_content = new JobContent("Junior Backend Developer", "description", "Stripe", Arrays.asList("Java"), Arrays.asList("SWE"), Arrays.asList("Leadership"), 4000, "Romania");
        //String employer_id = "Stripe";

        mockMvc.perform(post("/job")
            .content(new ObjectMapper().writeValueAsString(job_content))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            //.andExpect(jsonPath("$.id").value("3"))
            .andExpect(jsonPath("$.title").value("Junior Backend Developer"))
            .andExpect(jsonPath("$.salary").value(4000));
    }

    @Test
    public void testUpdateJob() throws Exception {
        JobContent job_content = new JobContent("Software Developer", "description", "Adobe", Arrays.asList("Java", "C#"), Arrays.asList("SWE"), Arrays.asList("Leadership", "Problem Solving"), 5000, "Romania");

        mockMvc.perform(put("/job/1")
            //.param("employerId", "1")
            .content(new ObjectMapper().writeValueAsString(job_content))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("Software Developer"))
            .andExpect(jsonPath("$.abilities[1]").value("C#"));

        mockMvc.perform(get("/job"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].title").value("Software Developer"))
            .andExpect(jsonPath("$[0].abilities[1]").value("C#"));
    }

    @Test
    public void testDeleteJob() throws Exception {
        mockMvc.perform(delete("/job/1"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/job"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("QA Engineer"));
    }


}
