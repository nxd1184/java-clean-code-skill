package examples.before;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class FatUserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public User createUser(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @RequestParam boolean isAdmin) {
        if (email == null || email.length() < 5) {
            throw new RuntimeException("bad email");
        }
        if (password == null || password.length() < 8) {
            throw new RuntimeException("bad password");
        }
        User u = new User();
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(password);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPhoneNumber(phoneNumber);
        u.setRole(isAdmin ? "ADMIN" : "USER");
        return userRepository.save(u);
    }
}

@Entity
class User {
    @Id @GeneratedValue Long id;
    String email, username, password, firstName, lastName, phoneNumber, role;
    // getters/setters omitted for brevity
}

interface UserRepository extends org.springframework.data.jpa.repository.JpaRepository<User, Long> {}
