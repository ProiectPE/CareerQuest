package ro.unibuc.hello.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.dto.User;
import ro.unibuc.hello.dto.UserCreation;
import ro.unibuc.hello.exception.InvalidEmailException;
import ro.unibuc.hello.exception.UserNotFoundException;
import ro.unibuc.hello.exception.UsernameTakenException;

public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    private static final String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public List<User> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> new User(user.getUsername(), user.getDescription(), user.getFirstName(), user.getLastName(), 
                    user.getBirthdate(), user.getEmail(), user.getPhone()))
                .collect(Collectors.toList());
    }

    public List<User> getAllUsersByName(String name) {
        List<UserEntity> users = userRepository.findByFullName(name);
        return users.stream()
                .map(user -> new User(user.getUsername(), user.getDescription(), user.getFirstName(), user.getLastName(), 
                    user.getBirthdate(), user.getEmail(), user.getPhone()))
                .collect(Collectors.toList());
    }

    public User getUser(String username) throws UserNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));
        return new User(user.getUsername(), user.getDescription(), user.getFirstName(), user.getLastName(),
                        user.getBirthdate(), user.getEmail(), user.getPhone());
    }

    public User createUser(UserCreation credentials) throws UsernameTakenException, InvalidEmailException {
        //check if user not already taken
        Optional<UserEntity> optionalUser = userRepository.findById(credentials.getUsername());
        optionalUser.ifPresent(user -> {throw new UsernameTakenException(user.getUsername());});

        //check if email is valid
        boolean validEmail = Pattern.compile(emailRegex)
                                    .matcher(credentials.getEmail())
                                    .matches();
        if(!validEmail) {
            throw new InvalidEmailException(credentials.getEmail());
        }

        //create user in database
        UserEntity user = new UserEntity(credentials.getUsername(), credentials.getPassword(), credentials.getEmail());
        userRepository.save(user);
        
        return new User(user.getUsername(), user.getEmail());
    }

    public User updateCredentials(String username, UserCreation credentials) throws UserNotFoundException, InvalidEmailException {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        UserEntity user = optionalUser.orElseThrow(() -> new UserNotFoundException(username));

        if(credentials.getEmail() != "") {
            boolean validEmail = Pattern.compile(emailRegex)
                                        .matcher(credentials.getEmail())
                                        .matches();
            if(!validEmail) {
                throw new InvalidEmailException(credentials.getEmail());
            }

            user.setEmail(credentials.getEmail());
        }

        if(credentials.getPassword() != "")
            user.setPassword(credentials.getPassword());

        return new User(user.getUsername(), user.getDescription(), user.getFirstName(), user.getLastName(),
            user.getBirthdate(), user.getEmail(), user.getPhone());
    }
}
