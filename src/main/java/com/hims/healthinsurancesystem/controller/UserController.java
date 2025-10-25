package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.UserService;
import com.hims.healthinsurancesystem.model.Role;
import com.hims.healthinsurancesystem.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;  // ✅ no generics

    // --- CREATE USER ---
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // --- GET USER BY ID ---
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- GET ALL USERS or filter by role ---
    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(name = "role", required = false) String role) {
        if (role == null || role.isBlank()) {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        }
        try {
            Role r = Role.valueOf(role.trim().toUpperCase());
            return ResponseEntity.ok(userService.findByRole(r));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid role: " + role);
        }
    }

    // --- UPDATE USER ---
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id); // Assuming setId() exists in User entity
        User updatedUser = userService.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    // --- DELETE USER ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
