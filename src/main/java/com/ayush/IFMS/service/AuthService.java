package com.ayush.IFMS.service;

import com.ayush.IFMS.dto.AuthRequest;
import com.ayush.IFMS.dto.AuthResponse;
import com.ayush.IFMS.model.User;
import com.ayush.IFMS.model.UserRole;
import com.ayush.IFMS.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        System.out.println("Attempting login for email: " + authRequest.getEmail());

        // Check if user exists
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> {
                    System.out.println("User not found!");
                    return new RuntimeException("User not found for email: " + authRequest.getEmail());
                });

        System.out.println("User found: " + user.getEmail());

        // Validate the password
        if (!user.getPassword().equals(authRequest.getPassword())) {
            System.out.println("Password mismatch!");
            throw new RuntimeException("Invalid credentials for email: " + authRequest.getEmail());
        }

        System.out.println("Login success!");

        AuthResponse response = new AuthResponse();
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole().name());
        response.setId(user.getId());
        response.setToken(UUID.randomUUID().toString());

        return response;
    }


    public AuthResponse registerUser(AuthRequest authRequest) {
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new RuntimeException("Email already in use: " + authRequest.getEmail());
        }

        User user = new User();
        user.setEmail(authRequest.getEmail());
        user.setPassword(authRequest.getPassword());
        user.setName(authRequest.getName());
        user.setRole(UserRole.valueOf(authRequest.getRole().toUpperCase())); 

        User savedUser = userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setEmail(savedUser.getEmail());
        response.setName(savedUser.getName());
        response.setRole(savedUser.getRole().name());
        response.setToken(UUID.randomUUID().toString());

        return response;
    }

    public void logoutUser(String token) {
    }
}
