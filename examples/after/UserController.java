package examples.after;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.register(request);
    }

    @PostMapping("/admins")
    public UserResponse createAdmin(@RequestBody CreateUserRequest request) {
        return userService.registerAdmin(request);
    }
}
