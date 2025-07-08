package org.example.githubfiles.model;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "repositories")
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "repo_name", nullable = false)
    private String repoName;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(nullable = false)
    private String url;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "up_to_date", nullable = false)
    private Boolean upToDate = true;

    public Repository() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getRepoName() { return repoName; }
    public void setRepoName(String repoName) { this.repoName = repoName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public Boolean getUpToDate() { return upToDate; }
    public void setUpToDate(Boolean upToDate) { this.upToDate = upToDate; }
}