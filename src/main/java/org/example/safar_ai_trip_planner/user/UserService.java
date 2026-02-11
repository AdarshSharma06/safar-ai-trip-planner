package org.example.safar_ai_trip_planner.user;

import org.example.safar_ai_trip_planner.common.exception.AuthenticationException;
import org.example.safar_ai_trip_planner.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public User registerUser(User user){

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            throw new AuthenticationException("Invalid email or password");
        }

        User user = userOptional.get();

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new AuthenticationException("Invalid email or password");
        }
        return jwtService.generateToken(user.getEmail());
    }
}
