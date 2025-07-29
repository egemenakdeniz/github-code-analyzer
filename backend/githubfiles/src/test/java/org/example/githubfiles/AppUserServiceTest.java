package org.example.githubfiles;

import org.example.githubfiles.exception.badrequest.EmptyPasswordException;
import org.example.githubfiles.exception.badrequest.EmptyUsernameException;
import org.example.githubfiles.exception.conflict.UsernameAlreadyExistsException;
import org.example.githubfiles.model.AppUser;
import org.example.githubfiles.repository.AppUserRepository;
import org.example.githubfiles.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveUser_WhenValidInputs() {
        String username = "egemen";
        String rawPassword = "123456";
        String encodedPassword = "encoded";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        AppUser savedUser = AppUser.builder().username(username).password(encodedPassword).build();
        when(userRepository.save(any())).thenReturn(savedUser);

        AppUser result = userService.saveUser(username, rawPassword);

        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);

        verify(userRepository).existsByUsername(username);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any());
    }

    @Test
    void shouldThrow_WhenUsernameIsNull() {
        assertThatThrownBy(() -> userService.saveUser(null, "pass"))
                .isInstanceOf(EmptyUsernameException.class)
                .hasMessageContaining("Username must not be empty.");
    }

    @Test
    void shouldThrow_WhenUsernameIsBlank() {
        assertThatThrownBy(() -> userService.saveUser("  ", "pass"))
                .isInstanceOf(EmptyUsernameException.class);
    }

    @Test
    void shouldThrow_WhenPasswordIsNull() {
        assertThatThrownBy(() -> userService.saveUser("egemen", null))
                .isInstanceOf(EmptyPasswordException.class)
                .hasMessageContaining("Password must not be empty.");
    }

    @Test
    void shouldThrow_WhenPasswordIsBlank() {
        assertThatThrownBy(() -> userService.saveUser("egemen", " "))
                .isInstanceOf(EmptyPasswordException.class);
    }

    @Test
    void shouldThrow_WhenUsernameExists() {
        when(userRepository.existsByUsername("egemen")).thenReturn(true);

        assertThatThrownBy(() -> userService.saveUser("egemen", "123"))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    void shouldReturnUser_WhenFound() {
        AppUser user = AppUser.builder().username("egemen").password("pwd").build();
        when(userRepository.findByUsername("egemen")).thenReturn(Optional.of(user));

        Optional<AppUser> result = userService.findByUsername("egemen");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("egemen");
    }

    @Test
    void shouldReturnEmpty_WhenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<AppUser> result = userService.findByUsername("unknown");

        assertThat(result).isEmpty();
    }
}