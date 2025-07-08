package org.example.githubfiles.repository;

import org.example.githubfiles.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.githubfiles.model.*;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    //List<Session> findByRepositoryId(Long repositoryId);
    boolean existsByRepository(Repository repository);

}
