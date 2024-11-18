package com.programmingtechie.taskmanagementsystem.service;

import com.programmingtechie.taskmanagementsystem.model.UserImpl;
import com.programmingtechie.taskmanagementsystem.model.usermodelenums.Role;
import com.programmingtechie.taskmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserImpl> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserImpl> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    //    @Transactional
//    public void register(UserImpl userImpl) {
//        userImpl.setPassword(passwordEncoder.encode(userImpl.getPassword()));
//        userImpl.setRole(Role.ROLE_USER);
//        userImpl.setRegistrationDate(LocalDateTime.now());
//        userRepository.save(userImpl);
//    }
    @Transactional
    public void registerUser(UserImpl user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setRegistrationDate(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void assignAdminRole(Long userId) {
        UserImpl user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);
    }
}
