package org.example.githubfiles.repository;

import jakarta.transaction.Transactional;
import org.example.githubfiles.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    //Repository findByUrl(String url);
    Optional<Repository> findByUserNameAndRepoNameAndBranchName(String userName, String repoName, String branchName);

    @Modifying
    @Transactional
    @Query(value = "UPDATE repositories SET up_to_date = false WHERE id = :id", nativeQuery = true)
    void markAsOutdated(@Param("id") Long id);
}
