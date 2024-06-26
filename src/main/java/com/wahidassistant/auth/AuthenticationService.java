package com.wahidassistant.auth;

import com.wahidassistant.config.JwtService;
import com.wahidassistant.model.Role;
import com.wahidassistant.model.User;
import com.wahidassistant.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
// A service for authentication. Author Pehr Nortén.
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Register a new user. Author Pehr Nortén.
    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        // Check if the username already exists
        if (repository.findByUsername((request.getUsername())).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists"); // Return a 409 Conflict status code if the username already exists
        }

        // Create a new user
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);

        // Set JWT token as cookie
        Cookie cookie = new Cookie("auth_token", jwtToken);
        cookie.setPath("/");
        cookie.setMaxAge(jwtService.getExpirationTimeInSeconds());
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        // Return the JWT token
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    // Authenticate a user. Author Pehr Nortén.
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = repository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found")); // Return a 404 Not Found status code if the user is not found
        var jwtToken = jwtService.generateToken(user);

        // Set JWT token as cookie
        Cookie cookie = new Cookie("auth_token", jwtToken);
        cookie.setPath("/");
        cookie.setMaxAge(jwtService.getExpirationTimeInSeconds());
        System.out.println(jwtService.getExpirationTimeInSeconds());
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        // Return the JWT token
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
