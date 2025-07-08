package org.example.githubfiles.dto;

public class RepositoryCheckResponse {
    public String repo;
    public boolean hasChanged;

    public RepositoryCheckResponse(String repo, boolean hasChanged) {
        this.repo = repo;
        this.hasChanged = hasChanged;
    }
}
