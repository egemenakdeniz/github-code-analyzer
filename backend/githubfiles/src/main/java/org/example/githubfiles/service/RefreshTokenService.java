package org.example.githubfiles.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.githubfiles.model.AppUser;
import org.example.githubfiles.model.RefreshToken;
import org.example.githubfiles.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository tokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(AppUser user) {
        tokenRepository.deleteByUser(user);

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        return tokenRepository.save(token);
    }

    public boolean isValid(RefreshToken token) {
        return token.getExpiryDate().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void deleteByToken(String token) {
        tokenRepository.deleteByToken(token);
    }
    public Optional<RefreshToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
