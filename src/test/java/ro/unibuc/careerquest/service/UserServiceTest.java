package ro.unibuc.careerquest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import ro.unibuc.careerquest.data.ApplicationEntity;
import ro.unibuc.careerquest.data.ApplicationRepository;
import ro.unibuc.careerquest.data.CVEntity;
import ro.unibuc.careerquest.data.CVRepository;
import ro.unibuc.careerquest.data.UserEntity;
import ro.unibuc.careerquest.data.UserRepository;
import ro.unibuc.careerquest.dto.CV;
import ro.unibuc.careerquest.dto.CVCreation;
import ro.unibuc.careerquest.dto.User;
import ro.unibuc.careerquest.dto.UserCreation;
import ro.unibuc.careerquest.exception.InvalidEmailException;
import ro.unibuc.careerquest.exception.InvalidPasswordException;
import ro.unibuc.careerquest.exception.UserNotFoundException;
import ro.unibuc.careerquest.exception.UsernameTakenException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {
    
    @Mock 
    private UserRepository userRepository;

    @Mock
    private CVRepository cvRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_getAllUsers() {
        //initialize users in database
        List<UserEntity> userEntities = Arrays.asList(new UserEntity("user1", "Parola1@", "user1@email.com"), new UserEntity("user2", "Parola1@", "user2@email.com"));
        
        when(userRepository.findAll()).thenReturn(userEntities);
        
        //get all users
        List<User> users = userService.getAllUsers();

        //verify data
        assertNotNull(users);
        assertEquals("user1", users.get(0).getUsername());
        assertEquals("user1@email.com", users.get(0).getEmail());
        assertEquals("user2", users.get(1).getUsername());
        assertEquals("user2@email.com", users.get(1).getEmail());
    }

    @Test
    void test_getAllUsersByName() {
        //initialize users in database
        String name = "Fabian";
        List<UserEntity> userEntities = Arrays.asList(new UserEntity("user1", "Parola1@", null, "Fabian", "Anghel", null, "user1@email.com", null));
        
        when(userRepository.findByFullNameContaining(name)).thenReturn(userEntities);
        
        //get all users with that name
        List<User> users = userService.getAllUsersByName(name);

        //verify data
        assertNotNull(users);
        assertEquals("user1", users.get(0).getUsername());
        assertEquals("user1@email.com", users.get(0).getEmail());
        assertEquals("Fabian", users.get(0).getFirstName());
        assertEquals("Anghel", users.get(0).getLastName());
        assertThat(users.get(0).getName(), containsString(name));
    }

    @Test
    void test_getUser() {
        //initialize user in database
        String username = "user1";
        String nonExistingUsername = "user2";
        UserEntity userEntity = new UserEntity(username, "Parola1@", "user1@email.com");

        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));
        when(userRepository.findById(nonExistingUsername)).thenReturn(Optional.empty());

        //get user
        User user = userService.getUser(username);

        //verify data
        assertNotNull(user);
        assertEquals("user1", user.getUsername());
        assertEquals("user1@email.com", user.getEmail());

        //exception for nonexisting user
        assertThrows(UserNotFoundException.class, () -> userService.getUser(nonExistingUsername));
    }

    @Test
    void test_createUser() {
        //initialize data for creation and exception cases
        UserCreation userData = new UserCreation("user1", "Parola1@", "user1@email.com");
        UserCreation userTakenData = new UserCreation("usertaken", "Parola1@", "user1@email.com");
        UserEntity userTakenEntity = new UserEntity("usertaken", "Parola1@", "user1@email.com");
        UserCreation badPassword1User = new UserCreation("user2", "Paro1@", "user2@email.com");
        UserCreation badPassword2User = new UserCreation("user3", "parola1@", "user3@email.com");
        UserCreation badPassword3User = new UserCreation("user4", "PAROLA1@", "user4@email.com");
        UserCreation badPassword4User = new UserCreation("user5", "Parola@@", "user5@email.com");
        UserCreation badPassword5User = new UserCreation("user6", "Parola11", "user6@email.com");
        UserCreation badEmailUser = new UserCreation("user7", "Parola1@", "bademail");
        
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity("user1", "Parola1@", "user1@email.com"));
        when(userRepository.findById("usertaken")).thenReturn(Optional.of(userTakenEntity));
        
        //create user
        User user = userService.createUser(userData);

        //verify creation
        assertNotNull(user);
        assertEquals("user1", user.getUsername());
        assertEquals("user1@email.com", user.getEmail());

        //verify that correct exception is thrown in all cases
        assertThrows(InvalidPasswordException.class, () -> userService.createUser(badPassword1User));
        assertThrows(InvalidPasswordException.class, () -> userService.createUser(badPassword2User));
        assertThrows(InvalidPasswordException.class, () -> userService.createUser(badPassword3User));
        assertThrows(InvalidPasswordException.class, () -> userService.createUser(badPassword4User));
        assertThrows(InvalidPasswordException.class, () -> userService.createUser(badPassword5User));
        assertThrows(InvalidEmailException.class, () -> userService.createUser(badEmailUser));
        assertThrows(UsernameTakenException.class, () -> userService.createUser(userTakenData));
    }

    @Test
    void test_updateCredentials() {
        //initialize data for creation, update and exception cases
        String username = "user1";
        String nonExistingUsername = "user2";
        UserEntity userEntity = new UserEntity(username, "Parola1@", "user1@email.com");
        UserCreation userData = new UserCreation(null, null, "new@email.com");
        UserCreation badPassword1User = new UserCreation(username, "Paro1@", "user2@email.com");
        UserCreation badPassword2User = new UserCreation(username, "parola1@", "user3@email.com");
        UserCreation badPassword3User = new UserCreation(username, "PAROLA1@", "user4@email.com");
        UserCreation badPassword4User = new UserCreation(username, "Parola@@", "user5@email.com");
        UserCreation badPassword5User = new UserCreation(username, "Parola11", "user6@email.com");
        UserCreation badEmailUser = new UserCreation(username, "Parola1@", "bademail");
        
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));
        when(userRepository.findById(nonExistingUsername)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity(username, "Parola1@", "new@email.com"));
        
        //update user
        User user = userService.updateCredentials(username, userData);

        //verify update
        assertNotNull(user);
        assertEquals("user1", user.getUsername());
        assertEquals("new@email.com", user.getEmail());

         //verify that correct exception is thrown in all cases
        assertThrows(InvalidPasswordException.class, () -> userService.updateCredentials(username, badPassword1User));
        assertThrows(InvalidPasswordException.class, () -> userService.updateCredentials(username, badPassword2User));
        assertThrows(InvalidPasswordException.class, () -> userService.updateCredentials(username, badPassword3User));
        assertThrows(InvalidPasswordException.class, () -> userService.updateCredentials(username, badPassword4User));
        assertThrows(InvalidPasswordException.class, () -> userService.updateCredentials(username, badPassword5User));
        assertThrows(InvalidEmailException.class, () -> userService.updateCredentials(username, badEmailUser));
        assertThrows(UserNotFoundException.class, () -> userService.updateCredentials(nonExistingUsername, userData));
    }

    @Test
    void test_updateUser() {
        //initialize current user
        String username = "user1";
        String nonExistingUsername = "user2";
        UserEntity userEntity = new UserEntity(username, "Parola1@", "user1@email.com");
        
        //initialize update data
        User userUpdate = new User() ;
        userUpdate.setBirthdate(LocalDate.of(2003,4,2));
        userUpdate.setFirstName("Fabian");
        userUpdate.setLastName("Anghel");
        
        //database operations
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));
        when(userRepository.findById(nonExistingUsername)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity(username, "Parola1@", null, 
            "Fabian", "Anghel", LocalDate.of(2003, 4, 2), "user1@email.com", null));
       
        //update user
        User user = userService.updateUser(username, userUpdate);

        //verify update
        assertNotNull(user);
        assertEquals("user1", user.getUsername());
        assertEquals("user1@email.com", user.getEmail());
        assertEquals("Fabian", user.getFirstName());
        assertEquals("Anghel", user.getLastName());
        assertEquals("Fabian Anghel", user.getName());
        assertEquals(LocalDate.of(2003,4,2), user.getBirthdate());
        assertEquals(22, user.getAge()); //to update on april 2nd

        //exception for nonexisting user
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(nonExistingUsername, userUpdate));
    }

    @Test
    void test_deleteUser() {
        //initialize user in database
        String username = "user1";
        String nonExistingUsername = "user2";
        UserEntity userEntity = new UserEntity(username, "Parola1@", "user1@email.com");
        
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));
        when(userRepository.findById(nonExistingUsername)).thenReturn(Optional.empty());

        //delete user
        userService.deleteUser(username);
        verify(userRepository, times(1)).delete(userEntity);

        //exception for nonexisting user
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistingUsername));
    }

    @Test
    void test_getCVs() {
        //initialize user and cvs in database
        String username = "user1";
        String nonExistingUsername = "user2";
        UserEntity userEntity = new UserEntity(username, "Parola1@", "user1@email.com");
        List<CVEntity> cvEntities = Arrays.asList(new CVEntity("1", username), new CVEntity("2", username));
        
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));
        when(userRepository.findById(nonExistingUsername)).thenReturn(Optional.empty());
        when(cvRepository.findByUserId(username)).thenReturn(cvEntities);
        
        //get cvs
        List<CV> cvs = userService.getCVs(username);

        //verify cvs
        assertNotNull(cvs);
        assertEquals("1", cvs.get(0).getId());
        assertEquals(username, cvs.get(0).getUserId());
        assertEquals("2", cvs.get(1).getId());
        assertEquals(username, cvs.get(1).getUserId());

        //exception for nonexisting user
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistingUsername));
    }

    @Test
    void test_addCV() {
        //initialize user in database and cv data
        String username = "user1";
        String nonExistingUsername = "user2";
        UserEntity userEntity = new UserEntity(username, "Parola1@", "user1@email.com");
        CVCreation cvData = new CVCreation("description", "achievements");
        CVEntity cvEntity = new CVEntity("1", username, "description", "achievements");
        
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));
        when(userRepository.findById(nonExistingUsername)).thenReturn(Optional.empty());
        when(cvRepository.save(any(CVEntity.class))).thenReturn(cvEntity);
        
        //add cv
        CV cv = userService.addCV(username, cvData);

        //verify added cv
        assertNotNull(cv);
        assertEquals("1", cv.getId());
        assertEquals(username, cv.getUserId());
        assertEquals("description", cv.getDescription());
        assertEquals("achievements", cv.getAchievements());

        //exception for nonexisting user
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistingUsername));
    }

    @Test
    void test_getApplications() {
        //initialize applications in database
        String username = "user1";
        List<ApplicationEntity> applicationEntities = Arrays.asList(new ApplicationEntity("1", "1", username, "1"), new ApplicationEntity("2", "2", username, "1"));
        
        when(applicationRepository.findByUsername(username)).thenReturn(applicationEntities);
        
        //get all of user's applications
        List<ApplicationEntity> applications = userService.getApplications(username);

        //verify applications
        assertNotNull(applications);
        assertEquals("1", applications.get(0).getId());
        assertEquals("1", applications.get(0).getJobId());
        assertEquals(username, applications.get(0).getUsername());
        assertEquals("1", applications.get(0).getCVId());
        assertEquals("2", applications.get(1).getId());
        assertEquals("2", applications.get(1).getJobId());
        assertEquals(username, applications.get(1).getUsername());
        assertEquals("1", applications.get(1).getCVId());
    }
}
