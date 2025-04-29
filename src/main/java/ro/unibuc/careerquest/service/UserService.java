package ro.unibuc.careerquest.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ro.unibuc.careerquest.data.UserRepository;
import ro.unibuc.careerquest.data.CVEntity;
import ro.unibuc.careerquest.data.CVRepository;
import ro.unibuc.careerquest.data.UserEntity;
import ro.unibuc.careerquest.dto.CV;
import ro.unibuc.careerquest.dto.CVCreation;
import ro.unibuc.careerquest.dto.User;
import ro.unibuc.careerquest.dto.UserCreation;
import ro.unibuc.careerquest.data.ApplicationEntity;
import ro.unibuc.careerquest.data.ApplicationRepository;
import ro.unibuc.careerquest.exception.InvalidEmailException;
import ro.unibuc.careerquest.exception.InvalidPasswordException;
import ro.unibuc.careerquest.exception.UserNotFoundException;
import ro.unibuc.careerquest.exception.UsernameTakenException;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MeterRegistry metricsRegistry;

    private final AtomicLong counter = new AtomicLong();
    private final AtomicLong userCounter = new AtomicLong();

    private static final String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern emailPattern = Pattern.compile(emailRegex);
    
    private static final Pattern uppercasePattern = Pattern.compile("[A-Z]");
    private static final Pattern lowercasePattern = Pattern.compile("[a-z]");
    private static final Pattern digitPattern = Pattern.compile("[0-9]");
    private static final Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");

    public List<User> getAllUsers() {
        final long startTime = System.currentTimeMillis();
        List<UserEntity> users = userRepository.findAll();
        final long endTime = System.currentTimeMillis();

        metricsRegistry.timer("time_get_all_users").record(endTime - startTime, TimeUnit.MILLISECONDS);

        return users.stream()
                .map(user -> new User(user))
                .collect(Collectors.toList());
    }

    public List<User> getAllUsersByName(String name) {
        final long startTime = System.currentTimeMillis();
        List<UserEntity> users = userRepository.findByFullNameContaining(name);
        final long endTime = System.currentTimeMillis();

        metricsRegistry.timer("time_get_users_by_name").record(endTime - startTime, TimeUnit.MILLISECONDS);

        return users.stream()
                .map(user -> new User(user))
                .collect(Collectors.toList());
    }

    public User getUser(String username) throws UserNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));

        metricsRegistry.counter("nr_profile_views", "username", username).increment();

        return new User(user);
    }

    public User createUser(UserCreation credentials) throws UsernameTakenException, InvalidEmailException {
        //check if user not already taken
        Optional<UserEntity> optionalUser = userRepository.findById(credentials.getUsername());
        optionalUser.ifPresent(user -> {throw new UsernameTakenException(user.getUsername());});

        //check if email is valid
        boolean validEmail = emailPattern.matcher(credentials.getEmail()).matches();
        if(!validEmail)
            throw new InvalidEmailException(credentials.getEmail());

        //check if password is valid
        String password = credentials.getPassword();
        boolean passMinLength = password.length() >= 8;
        boolean hasUppercase = uppercasePattern.matcher(password).find();
        boolean hasLowercase = lowercasePattern.matcher(password).find();
        boolean hasDigit = digitPattern.matcher(password).find();
        boolean hasSpecial = specialCharPattern.matcher(password).find();

        if(!passMinLength || !hasUppercase || !hasLowercase || !hasDigit || !hasSpecial)
            throw new InvalidPasswordException();

        //create user in database
        UserEntity user = new UserEntity(credentials.getUsername(), credentials.getPassword(), credentials.getEmail());
        userRepository.save(user);

        metricsRegistry.gauge("nr_of_users", userCounter.incrementAndGet());
        
        return new User(user.getUsername(), user.getEmail());
    }

    public User updateCredentials(String username, UserCreation credentials) throws UserNotFoundException, InvalidEmailException {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));

        if(credentials.getEmail() != null) {
            boolean validEmail = emailPattern.matcher(credentials.getEmail()).matches();
            if(!validEmail) {
                throw new InvalidEmailException(credentials.getEmail());
            }

            user.setEmail(credentials.getEmail());
        }

        if(credentials.getPassword() != null) {
            String password = credentials.getPassword();
            boolean passMinLength = password.length() >= 8;
            boolean hasUppercase = uppercasePattern.matcher(password).matches();
            boolean hasLowercase = lowercasePattern.matcher(password).matches();
            boolean hasDigit = digitPattern.matcher(password).matches();
            boolean hasSpecial = specialCharPattern.matcher(password).matches();

            if(!passMinLength || !hasUppercase || !hasLowercase || !hasDigit || !hasSpecial)
                throw new InvalidPasswordException();

            user.setPassword(password);
        }

        userRepository.save(user);

        return new User(user);
    }

    public User updateUser(String username, User userData) throws UserNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));

        if(userData.getDescription() != null)
            user.setDescription(userData.getDescription());
        if(userData.getFirstName() != null)
            user.setFirstName(userData.getFirstName());
        if(userData.getLastName() != null)
            user.setLastName(userData.getLastName());
        if(userData.getBirthdate() != null)
            user.setBirthdate(userData.getBirthdate());
        if(userData.getPhone() != null)
            user.setPhone(userData.getPhone());

        userRepository.save(user);

        return new User(user);
    }  

    public void deleteUser(String username) throws UserNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));

        metricsRegistry.gauge("nr_of_users", userCounter.decrementAndGet());

        userRepository.delete(user);
    }

    public List<CV> getCVs(String username) throws UserNotFoundException {
        //first check if user exists
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));
        
        List<CVEntity> cvs = cvRepository.findByUserId(username);

        return cvs.stream()
                .map(cv -> new CV(cv))
                .collect(Collectors.toList());
    }

    public CV addCV(String username, CVCreation cvData) throws UserNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));

        CVEntity cv = new CVEntity(Long.toString(counter.incrementAndGet()), username, cvData.getDescription(), cvData.getAchievements());
        cvRepository.save(cv);

        return new CV(cv.getId(), username, cv.getDescription(), cv.getAchievements());
    }

    public List<ApplicationEntity> getApplications(String username) {
        List<ApplicationEntity> applications = applicationRepository.findByUsername(username);
        return applications;
    }
}
