package ro.unibuc.hello.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String userNotFoundTemplate = "User %s was not found";

    public UserNotFoundException(String entity) {
        super(String.format(userNotFoundTemplate, entity));
    }
}
