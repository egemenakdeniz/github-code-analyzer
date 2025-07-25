package org.example.githubfiles.repository;

import org.example.githubfiles.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.githubfiles.model.*;

public interface SessionRepository extends JpaRepository<Session, Long> {
    boolean existsByRepository(Repository repository);

}
