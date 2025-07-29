package org.example.githubfiles.repository;

import jakarta.transaction.Transactional;
import org.example.githubfiles.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findBySession_Repository(Repository repository);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO reports (generated_at, file_data, analysis_session_id) VALUES (:createdAt, :fileData, :sessionId)", nativeQuery = true)
    void insertReport(@Param("createdAt") LocalDateTime createdAt,
                      @Param("fileData") byte[] fileData,
                      @Param("sessionId") Long sessionId);

    @Query(value = "SELECT file_data FROM reports WHERE id = :id", nativeQuery = true)
    byte[] findFileDataById(@Param("id") Long id);

    @Query(value = """
    SELECT 
        r.id, 
        s.model_name, 
        r.generated_at
    FROM reports r
    JOIN analysis_sessions s ON r.analysis_session_id = s.id
    JOIN repositories repo ON s.repository_id = repo.id
    WHERE repo.user_name = :owner AND repo.repo_name = :repo AND repo.branch_name = :branch
    """, nativeQuery = true)
    List<Object[]> findReportPreviewRaw(
            @Param("owner") String owner,
            @Param("repo") String repo,
            @Param("branch") String branch
    );
}
