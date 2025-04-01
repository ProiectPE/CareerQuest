package ro.unibuc.careerquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ro.unibuc.careerquest.dto.Employer;
import ro.unibuc.careerquest.service.EmployerService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class Employer_IntegrationTest {

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
        final String MONGO_URL = "mongodb://localhost:";
        final String PORT = String.valueOf(mongoDBContainer.getMappedPort(27017));

        registry.add("mongodb.connection.url", () -> MONGO_URL + PORT);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployerService employerService;

    @BeforeEach
    public void cleanUpAndAddTestData() {
        List<Employer> employers = employerService.getAllEmployers();
        for (Employer employer : employers)
            employerService.deleteEmployer(employer.getId());

        Employer employer1 = new Employer(UUID.randomUUID().toString(), "Employer1", "employer1@email.com", "1234567890", "Tech Corp", LocalDate.now(), true);
        Employer employer2 = new Employer(UUID.randomUUID().toString(), "Employer2", "employer2@email.com", "0987654321", "Finance Corp", LocalDate.now(), false);

        employerService.saveEmployer(employer1);
        employerService.saveEmployer(employer2);
    }

    @Test
    public void testGetAllEmployers() throws Exception {
        mockMvc.perform(get("/employer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testCreateEmployer() throws Exception {
        Employer employer3 = new Employer(UUID.randomUUID().toString(), "Employer3", "employer3@email.com", "1122334455", "Retail Corp", LocalDate.now(), true);

        mockMvc.perform(post("/employer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employer3)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Employer3"));
    }

    @Test
    public void testUpdateEmployer() throws Exception {
        Employer updatedEmployer = new Employer(UUID.randomUUID().toString(), "Updated Employer", "updated@email.com", "5544332211", "Updated Corp", LocalDate.now(), false);

        mockMvc.perform(put("/employer/" + updatedEmployer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedEmployer)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Employer"));
    }

    @Test
    public void testDeleteEmployer() throws Exception {
        mockMvc.perform(delete("/employer/" + UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }
}
