package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Role;
import com.hims.healthinsurancesystem.model.User;
import com.hims.healthinsurancesystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Save or update a user
    public User save(User user) {
        return userRepository.save(user);
    }

    // Get user by ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Get all users
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    // Delete user by ID
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Find by username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Find by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Check if username exists
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // Check if email exists
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
