package org.example.githubfiles;

import org.example.githubfiles.exception.notfound.UserNotFoundException;
import org.example.githubfiles.model.AppUser;
import org.example.githubfiles.repository.AppUserRepository;
import org.example.githubfiles.service.AppUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AppUserDetailsServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService userDetailsService;

    @Mock
    private AppUser user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new AppUser();
        user.setUsername("egemen");
        user.setPassword("encodedPassword123");
    }

    @Test
    void shouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByUsername("egemen")).thenReturn(Optional.of(user));

        var userDetails = userDetailsService.loadUserByUsername("egemen");

        assertThat(userDetails.getUsername()).isEqualTo("egemen");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword123");
        assertThat(userDetails.getAuthorities()).isEmpty();

        verify(userRepository).findByUsername("egemen");
    }

    @Test
    void shouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Kullanıcı bulunamadı: unknown");

        verify(userRepository).findByUsername("unknown");
    }
}
