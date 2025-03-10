package ro.unibuc.hello.exception;

public class UsernameTakenException extends RuntimeException {
    private static final String usernameTakenTemplate = "Username %s already taken";

    public UsernameTakenException(String entity) {
        super(String.format(usernameTakenTemplate, entity));
    }
}
