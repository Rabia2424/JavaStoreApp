package com.store.storeapp.Services.impl;

import com.store.storeapp.Models.User;
import com.store.storeapp.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserById(Long userId){
        return userRepository.findById(userId);
    }
    public Optional<User> findByUserId(Long userId) { return userRepository.findById(userId); }

    public User updateUser(User user){ return userRepository.save(user); }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }
}
