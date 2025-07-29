package org.example.githubfiles;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@OpenAPIDefinition(
        info = @Info(
                title = "GitHub Code Analyzer API",
                version = "1.0",
                description = "This API analyzes code repositories on GitHub and generates PDF reports.",
                contact = @Contact(
                        name = "Egemen Akdeniz",
                        url = "https://github.com/egemenakdeniz"
                )
        )
)
@SpringBootApplication
public class GithubfilesApplication {
    public static void main(String[] args) {
        SpringApplication.run(GithubfilesApplication.class, args);
    }

}
