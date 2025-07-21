package org.example.githubfiles.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
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

}
