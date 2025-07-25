package org.example.githubfiles.repository;

import org.example.githubfiles.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;



public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByRepositoryIdAndIsActiveTrue(Long repositoryId);
    Optional<File> findByPathAndRepository_IdAndIsActiveTrue(String path, Long repositoryId);

    @Transactional
    @Modifying
    @Query("UPDATE File f SET f.isActive = false WHERE f.repository.id = :repoId AND f.isActive = true")
    void deactivateAllByRepositoryId(@Param("repoId") Long repoId);
}