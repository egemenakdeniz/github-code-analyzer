package org.example.githubfiles.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// @Getter @Setter //Lombok
@Entity
@Table(name = "analysis_sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "repository_id",nullable = false)
    private Repository repository;

    @Column(nullable = false)
    private String model_name;

    @Column(name = "prompt", columnDefinition = "text", nullable = false)
    private String prompt;

    @Column(nullable = false)
    private LocalDateTime executed_at;

    public LocalDateTime getExecuted_at() {return executed_at;}

    public void setExecuted_at(LocalDateTime executed_at) {this.executed_at = executed_at;}

    public String getPrompt() {return prompt;}

    public void setPrompt(String prompt) {this.prompt = prompt;}

    public String getModel_name() {return model_name;}

    public void setModel_name(String model_name) {this.model_name = model_name;}

    public Repository getRepository() {return repository;}

    public void setRepository(Repository repository) {this.repository = repository;}

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Session() {}
}
