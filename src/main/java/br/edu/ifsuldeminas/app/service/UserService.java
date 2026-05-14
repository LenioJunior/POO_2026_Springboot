package br.edu.ifsuldeminas.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.edu.ifsuldeminas.app.model.User;
import br.edu.ifsuldeminas.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(User user) {
        userRepository.save(user);
        return user;
    }

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public User update(User user) {
        Optional<User> optionalUser = getById(user.getId());

        if (optionalUser.isPresent()) {
            User userDb = optionalUser.get();
            userDb.setName(user.getName());
            userDb.setAge(user.getAge());

            try {
                userDb = userRepository.save(userDb);
                return userDb;
            } catch (DataIntegrityViolationException e) {   
                e.printStackTrace();             
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new EntityNotFoundException(String.format("The user with id '%i' was not found!", user.getId()));
        }
    }

    public User delete(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User userDb = optionalUser.get();

            try {
                userRepository.delete(userDb);
                return userDb;
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new EntityNotFoundException("Entity with ID " + id + " not found.");
        }
    }
}
