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
@Table(name = "analysis_results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "analysis_session_id",nullable = false)
    private Session session;

    @ManyToOne(optional = false)
    @JoinColumn(name = "file_id",nullable = false)
    private File file;

    @Column(columnDefinition = "TEXT")
    private String class_name;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String severity;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String issue;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(nullable = false)
    private LocalDateTime analyzed_at;

}
