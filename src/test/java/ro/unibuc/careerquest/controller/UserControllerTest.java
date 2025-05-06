package ro.unibuc.careerquest.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.ServletException;
import ro.unibuc.careerquest.data.ApplicationEntity;
import ro.unibuc.careerquest.dto.CV;
import ro.unibuc.careerquest.dto.CVCreation;
import ro.unibuc.careerquest.dto.User;
import ro.unibuc.careerquest.dto.UserCreation;
import ro.unibuc.careerquest.exception.InvalidEmailException;
import ro.unibuc.careerquest.exception.InvalidPasswordException;
import ro.unibuc.careerquest.service.UserService;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void test_getAllUsers() throws Exception {
        List<User> users = Arrays.asList(new User("user1", "user1@email.com"), new User("user2", "user2@email.com"));
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[0].email").value("user1@email.com"))
            .andExpect(jsonPath("$[1].username").value("user2"))
            .andExpect(jsonPath("$[1].email").value("user2@email.com"));
    }

    @Test
    void test_getAllUsersByName() throws Exception {
        String name = "Fabian";
        List<User> users = Arrays.asList(new User("user1", null, "Fabian", "Anghel", null, "user1@email.com", null));
        when(userService.getAllUsersByName(name)).thenReturn(users);


        mockMvc.perform(get("/users/{name}", name))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[0].email").value("user1@email.com"))
            .andExpect(jsonPath("$[0].firstName").value("Fabian"))
            .andExpect(jsonPath("$[0].lastName").value("Anghel"));
    }

    @Test
    void test_getUser() throws Exception {
        String username = "user1";
        User user = new User(username, "user1@email.com");
        when(userService.getUser(username)).thenReturn(user);

        mockMvc.perform(get("/user/{id}", username))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.email").value("user1@email.com"));
    }

    @Test
    void test_createUser() throws Exception {
        User user = new User("user1", "user1@email.com");
        when(userService.createUser(any(UserCreation.class))).thenReturn(user);

        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        //Mock behavior for invalid email
        doThrow(new InvalidEmailException("bademail"))
            .when(userService).createUser(argThat(c -> !emailPattern.matcher(c.getEmail()).matches()));

        //Mock behavior for invalid password
        doThrow(new InvalidPasswordException())
            .when(userService).createUser(argThat(c -> {
                String password = c.getPassword();
                return password.length() < 8 ||
                    !password.matches(".*[A-Z].*") ||   // At least one uppercase
                    !password.matches(".*[a-z].*") ||   // At least one lowercase
                    !password.matches(".*\\d.*") ||     // At least one digit
                    !password.matches(".*[@#$%^&+=!].*"); // At least one special character
            }));

        //bad password
        assertThrows(ServletException.class,
        () -> mockMvc.perform(post("/user")
            .content("{\"username\": \"user1\", \"password\": \"12345678\", \"email\": \"user1@email.com\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidPasswordException))
        );

        //bad email
        assertThrows(ServletException.class,
        () -> mockMvc.perform(post("/user")
            .content("{\"username\": \"user1\", \"password\": \"Parola1@\", \"email\": \"bademail\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidEmailException))
        );

        //good user
        mockMvc.perform(post("/user")
            .content("{\"username\": \"user1\", \"password\": \"Parola1@\", \"email\": \"user1@email.com\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.email").value("user1@email.com"));
    }

    @Test
    void test_updateCredentials() throws Exception {
        String username = "user1";
        User updatedUser = new User(username, "user1@email.com");
        when(userService.updateCredentials(eq(username), any(UserCreation.class))).thenReturn(updatedUser);

        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        //Mock behavior for invalid email
        doThrow(new InvalidEmailException("bademail"))
            .when(userService).updateCredentials(eq(username), argThat(c -> !emailPattern.matcher(c.getEmail()).matches()));

        //Mock behavior for invalid password
        doThrow(new InvalidPasswordException())
            .when(userService).updateCredentials(eq(username), argThat(c -> {
                String password = c.getPassword();
                return password != null && 
                    (password.length() < 8 ||
                    !password.matches(".*[A-Z].*") ||   // At least one uppercase
                    !password.matches(".*[a-z].*") ||   // At least one lowercase
                    !password.matches(".*\\d.*") ||     // At least one digit
                    !password.matches(".*[@#$%^&+=!].*")); // At least one special character
            }));

        //bad password
        assertThrows(ServletException.class,
        () -> mockMvc.perform(put("/user-cred/{id}", username)
            .content("{ \"password\": \"12345678\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
        );

        //bad email
        assertThrows(ServletException.class,
        () -> mockMvc.perform(put("/user-cred/{id}", username)
            .content("{\"email\": \"bademail\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
        );

        //good user
        mockMvc.perform(put("/user-cred/{id}", username)
            .content("{\"email\": \"user1@email.com\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.email").value("user1@email.com"));
    }

    @Test 
    void test_updateUser() throws Exception {
        String username = "user1";
        User updatedUser = new User(username, "descriere", "Fabian", "Anghel", LocalDate.of(2003, 4, 2), "user1@email.com", null);
        when(userService.updateUser(eq(username), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/user/{id}", username)
            .content("{\"description\": \"descriere\", \"firstName\": \"Fabian\", \"lastName\": \"Anghel\", \"birthdate\": \"2003-04-02\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.description").value("descriere"))
            .andExpect(jsonPath("$.firstName").value("Fabian"))
            .andExpect(jsonPath("$.lastName").value("Anghel"))
            .andExpect(jsonPath("$.birthdate").value("2003-04-02"))
            .andExpect(jsonPath("$.age").value("22")) //change to 22 next week
            .andExpect(jsonPath("$.email").value("user1@email.com"));
    }

    @Test
    void test_deleteUser() throws Exception {
        String username = "user1";
        User user = new User(username, "user1@email.com");
        when(userService.createUser(any(UserCreation.class))).thenReturn(user);

        //first create user
        mockMvc.perform(post("/user")
            .content("{\"username\": \"user1\", \"password\": \"Parola1@\", \"email\": \"user1@email.com\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        
        //then delete the user
        mockMvc.perform(delete("/user/{id}", username))
            .andExpect(status().isOk());

        //check user is deleted
        verify(userService, times(1)).deleteUser(username);
    }

    @Test
    void test_getCVs() throws Exception {
        String username = "user1";
        List<CV> cvs = Arrays.asList(new CV("1", username), new CV("2", username));
        when(userService.getCVs(username)).thenReturn(cvs);

        mockMvc.perform(get("/user-cvs/{id}", username))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].userId").value(username))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].userId").value(username));
    }

    @Test
    void test_addCV() throws Exception {
        String username = "user1";
        CV cv = new CV("1", username, "descriere", "ceva");
        when(userService.addCV(eq(username), any(CVCreation.class))).thenReturn(cv);

        mockMvc.perform(post("/user-cvs/{id}", username)
            .content("{\"description\": \"descriere\", \"achievements\": \"ceva\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.userId").value(username))
            .andExpect(jsonPath("$.description").value("descriere"))
            .andExpect(jsonPath("$.achievements").value("ceva"));
    }

    @Test
    void test_getApplications() throws Exception {
        String username = "user1";
        List<ApplicationEntity> applications = Arrays.asList(new ApplicationEntity("1", "1", username, "1"), new ApplicationEntity("2", "2", username, "1"));
        when(userService.getApplications(username)).thenReturn(applications);

        mockMvc.perform(get("/user-app/{id}", username))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].jobId").value("1"))
            .andExpect(jsonPath("$[0].username").value(username))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].jobId").value("2"))
            .andExpect(jsonPath("$[1].username").value(username));
    }
}
