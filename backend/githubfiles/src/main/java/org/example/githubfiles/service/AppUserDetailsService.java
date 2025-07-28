package org.example.githubfiles.service;

import lombok.RequiredArgsConstructor;
import org.example.githubfiles.model.AppUser;
import org.example.githubfiles.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.example.githubfiles.exception.notfound.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList()) // Yetki sistemi kuracaksan burada role eklenir
                .build();
    }
}