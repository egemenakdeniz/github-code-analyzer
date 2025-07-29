package org.example.githubfiles.service;

import lombok.RequiredArgsConstructor;
import org.example.githubfiles.exception.badrequest.EmptyPasswordException;
import org.example.githubfiles.exception.badrequest.EmptyUsernameException;
import org.example.githubfiles.exception.conflict.UsernameAlreadyExistsException;
import org.example.githubfiles.model.AppUser;
import org.example.githubfiles.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser saveUser(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new EmptyUsernameException("Username must not be empty.");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new EmptyPasswordException("Password must not be empty.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already exists: " + username);
        }

        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .build();
        return userRepository.save(user);
    }

    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}