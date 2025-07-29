package org.example.githubfiles;

import org.example.githubfiles.model.AppUser;
import org.example.githubfiles.model.RefreshToken;
import org.example.githubfiles.repository.RefreshTokenRepository;
import org.example.githubfiles.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);
    }

    @Test
    void shouldCreateNewRefreshToken() {
        AppUser user = new AppUser();
        RefreshToken expectedToken = RefreshToken.builder()
                .token("mock-token")
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(expectedToken);

        RefreshToken token = refreshTokenService.createRefreshToken(user);

        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertNotNull(token.getExpiryDate());

        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldReturnTrueIfTokenIsValid() {
        RefreshToken token = RefreshToken.builder()
                .token("abc")
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();

        assertTrue(refreshTokenService.isValid(token));
    }

    @Test
    void shouldReturnFalseIfTokenIsNull() {
        assertFalse(refreshTokenService.isValid(null));
    }

    @Test
    void shouldReturnFalseIfTokenExpired() {
        RefreshToken token = RefreshToken.builder()
                .token("expired")
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();

        assertFalse(refreshTokenService.isValid(token));
    }

    @Test
    void shouldDeleteTokenByValue() {
        String token = "t1";
        refreshTokenService.deleteByToken(token);
        verify(refreshTokenRepository).deleteByToken(token);
    }

    @Test
    void shouldFindByToken() {
        String token = "t2";
        RefreshToken mockToken = RefreshToken.builder().token(token).build();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(mockToken));

        Optional<RefreshToken> found = refreshTokenService.findByToken(token);
        assertTrue(found.isPresent());
        assertEquals(token, found.get().getToken());
    }
}