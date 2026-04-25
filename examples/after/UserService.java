package examples.after;

public interface UserService {
    UserResponse register(CreateUserRequest request);
    UserResponse registerAdmin(CreateUserRequest request);
}
