package com.example.userservice.service;

import com.example.userservice.model.Users;
import com.example.userservice.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
   public Users createUser(Users user) {
        user.setPassword( user.getPassword() );
        return userRepository.save(user);
    }

    public Optional<Users> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Users login(String username, String password) {
        Users user = userRepository.findByUsername(username).orElse(null);
        if( user != null ){
            System.out.println( user.getUsername() + "-" + user.getPassword() + "-" + user.getPassword().equals(password) );
        }
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}