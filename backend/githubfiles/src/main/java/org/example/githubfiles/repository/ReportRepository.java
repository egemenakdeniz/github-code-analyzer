package org.example.githubfiles.repository;

import org.example.githubfiles.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    //List<Report> findBySessionId(Long sessionId);
    List<Report> findBySession_Repository(Repository repository);

}
