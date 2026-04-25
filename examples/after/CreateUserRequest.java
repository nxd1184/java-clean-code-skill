package examples.after;

public record CreateUserRequest(
        String email,
        String username,
        String password,
        String firstName,
        String lastName,
        String phoneNumber) {

    private static final int MIN_EMAIL_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;

    public CreateUserRequest {
        if (email == null || email.length() < MIN_EMAIL_LENGTH) {
            throw new IllegalArgumentException("email too short");
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("password too short");
        }
    }
}
