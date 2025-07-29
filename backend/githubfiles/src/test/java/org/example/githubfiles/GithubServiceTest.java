package org.example.githubfiles;

import org.example.githubfiles.exception.badrequest.InvalidRepositoryMetadataException;
import org.example.githubfiles.exception.internal.GithubTokenMissingException;
import org.example.githubfiles.exception.unauthorized.GithubAuthenticationException;
import org.example.githubfiles.exception.unavailable.NetworkUnavailableException;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.service.GithubService;
import org.example.githubfiles.utils.NetworkUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

    @InjectMocks
    private GithubService githubService;

    @Mock
    private RestTemplate restTemplate;

    private Repository dummyRepo;

    @BeforeEach
    void setUp() {
        dummyRepo = new Repository();
        dummyRepo.setUserName("egemen");
        dummyRepo.setRepoName("test-repo");
        dummyRepo.setBranchName("main");

        System.setProperty("GITHUB_TOKEN", "dummy-token");
    }

    @Test
    void shouldThrowWhenTokenIsMissing() {
        System.clearProperty("GITHUB_TOKEN");

        GithubTokenMissingException  ex = assertThrows(
                GithubTokenMissingException.class,
                () -> githubService.fetchFilesFromRepo(dummyRepo)
        );

        assertEquals("GitHub token is not set in environment variables", ex.getMessage());
    }

    @Test
    void shouldThrowWhenMetadataMissing() throws Exception {
        try (MockedStatic<NetworkUtils> staticMock = mockStatic(NetworkUtils.class)) {
            staticMock.when(NetworkUtils::isInternetAvailable).thenReturn(true);

            Field tokenField = GithubService.class.getDeclaredField("token");
            tokenField.setAccessible(true);
            tokenField.set(githubService, "dummy-token");

            Repository invalidRepo = new Repository(); // null username, reponame, branch

            assertThrows(InvalidRepositoryMetadataException.class, () ->
                    githubService.fetchFilesFromRepo(invalidRepo));
        }
    }

    @Test
    void shouldThrowWhenInternetIsNotAvailable() {
        try (MockedStatic<NetworkUtils> staticMock = org.mockito.Mockito.mockStatic(NetworkUtils.class)) {
            staticMock.when(NetworkUtils::isInternetAvailable).thenReturn(false);

            NetworkUnavailableException ex = assertThrows(NetworkUnavailableException.class, () ->
                    githubService.fetchFilesFromRepo(dummyRepo));

            assertEquals("The server is not connected to the internet. Please check your network connection.", ex.getMessage());
        }
    }



    @Test
    void shouldThrowRepositoryNotFoundFor404() throws Exception {
        try (MockedStatic<NetworkUtils> staticMock = mockStatic(NetworkUtils.class)) {
            staticMock.when(NetworkUtils::isInternetAvailable).thenReturn(true);

            GithubService service = new GithubService();
            Field tokenField = GithubService.class.getDeclaredField("token");
            tokenField.setAccessible(true);
            tokenField.set(service, "dummy-token"); // sahte token

            Repository repo = new Repository();
            repo.setUserName("this-user-should-not-exist");
            repo.setRepoName("this-repo-should-not-exist");
            repo.setBranchName("main");

            assertThrows(GithubAuthenticationException.class, () ->
                    service.fetchFilesFromRepo(repo));
        }
    }

    @Test
    void shouldThrowFileContentFetchExceptionWhenContentFails() throws Exception {
        try (MockedStatic<NetworkUtils> staticMock = mockStatic(NetworkUtils.class)) {
            staticMock.when(NetworkUtils::isInternetAvailable).thenReturn(true);

            // dummy token setle
            Field tokenField = GithubService.class.getDeclaredField("token");
            tokenField.setAccessible(true);
            tokenField.set(githubService, "dummy-token");

            Repository repo = new Repository();
            repo.setUserName("torvalds");
            repo.setRepoName("linux");
            repo.setBranchName("master");

            assertThrows(GithubAuthenticationException.class, () ->
                    githubService.fetchFilesFromRepo(repo));
        }
    }

    @Test
    void shouldThrowWhenInternetUnavailable() {
        try (MockedStatic<NetworkUtils> staticMock = mockStatic(NetworkUtils.class)) {
            staticMock.when(NetworkUtils::isInternetAvailable).thenReturn(false);

            Repository repo = new Repository();
            repo.setUserName("egemen");
            repo.setRepoName("test-repo");
            repo.setBranchName("main");

            NetworkUnavailableException ex = assertThrows(NetworkUnavailableException.class, () ->
                    githubService.fetchFilesFromRepo(repo));

            assertEquals("The server is not connected to the internet. Please check your network connection.", ex.getMessage());
        }
    }
}