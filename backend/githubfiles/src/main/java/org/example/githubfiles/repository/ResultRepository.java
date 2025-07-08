package org.example.githubfiles.repository;

import org.example.githubfiles.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    //List<Result> findBySessionId(Long sessionId);
    //List<Result> findByFileId(Long fileId);
    //List<Report> findBySession_Repository(Repository repository);

}
