package ro.unibuc.careerquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.careerquest.dto.User;
import ro.unibuc.careerquest.dto.UserCreation;
import ro.unibuc.careerquest.service.UserService;

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
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class UserControllerIntegrationTest {

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
        final String MONGO_URL = "mongodb://host.docker.internal:";
        final String PORT = String.valueOf(mongoDBContainer.getMappedPort(27017));

        registry.add("mongodb.connection.url", () -> MONGO_URL + PORT);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @BeforeEach 
    public void cleanUpAndAddTestData() {
        List<User> users = userService.getAllUsers();
        for(User user: users)
            userService.deleteUser(user.getUsername());

        UserCreation user1 = new UserCreation("user1", "Parola1@", "user1@email.com");
        UserCreation user2 = new UserCreation("user2", "Parola1@", "user2@email.com");

        userService.createUser(user1);
        userService.createUser(user2);
    }

    @Test 
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[0].email").value("user1@email.com"))
            .andExpect(jsonPath("$[1].username").value("user2"))
            .andExpect(jsonPath("$[1].email").value("user2@email.com"));
    }

    @Test
    public void createUser() throws Exception {
        UserCreation user3 = new UserCreation("user3", "Parola1@", "user3@email.com");

        mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(user3)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("user3"))
            .andExpect(jsonPath("$.email").value("user3@email.com"));

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    public void updateUser() throws Exception {
        User userUpdate = new User();
        userUpdate.setFirstName("Fabian");
        userUpdate.setLastName("Anghel");

        mockMvc.perform(put("/user/user1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(userUpdate)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.firstName").value("Fabian"))
            .andExpect(jsonPath("$.lastName").value("Anghel"));
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(delete("/user/user2"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[0].email").value("user1@email.com"));
    }
}
