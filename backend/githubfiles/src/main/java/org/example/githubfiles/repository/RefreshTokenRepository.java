package org.example.githubfiles.repository;

import org.example.githubfiles.model.AppUser;
import org.example.githubfiles.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByUser(AppUser user);
    void deleteByToken(String token);
    Optional<RefreshToken> findByToken(String token);


}
