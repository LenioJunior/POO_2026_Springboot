package br.edu.ifsuldeminas.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ifsuldeminas.app.model.User;
import br.edu.ifsuldeminas.app.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User not found with id: " + id);
            response.put("user", null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        user = userService.create(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestParam Long id, @RequestParam String name, @RequestParam int age) {

        User userToUpdate = new User(id, name, age);
        try {
            User user = userService.update(userToUpdate);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.delete(id);
            response.put("entity", user);
            response.put("message", "The user was delete sucessfuly.");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("message", "An error ocurred while deleting the user: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}