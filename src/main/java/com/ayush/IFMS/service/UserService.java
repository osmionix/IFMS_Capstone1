package com.ayush.IFMS.service;


import com.ayush.IFMS.dto.UserDTO;
import com.ayush.IFMS.model.User;
import com.ayush.IFMS.model.UserRole;
import com.ayush.IFMS.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findByRole(UserRole.valueOf(role)).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        return dto;
    }

    // ✅ Get user by email
    public UserDTO getUserByEmail(String email) {
        // Retrieve user by email
        Optional<User> userOptional = userRepository.findByEmail(email);

        // If the user is found, map to UserDTO and return
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setRole(user.getRole().name()); // Convert Enum to String
            return userDTO;
        } else {
            return null; // User not found
        }
    }
}