package org.example.safar_ai_trip_planner.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.safar_ai_trip_planner.user.dto.LoginRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService ;

    @PostMapping("/register")
    public User register(@Valid @RequestBody User user){
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request){
        String token = userService.login(request.getEmail(), request.getPassword());

        return Map.of("token", token);
    }

    @GetMapping("/me")
    public String me(){

       String email = (String) SecurityContextHolder
               .getContext()
               .getAuthentication()
               .getPrincipal();
       return "Logged in as : " + email;
    }
}
