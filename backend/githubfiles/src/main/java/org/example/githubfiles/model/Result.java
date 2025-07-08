package org.example.githubfiles.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;

import java.time.LocalDateTime;


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


    public LocalDateTime getAnalyzed_at() {return analyzed_at;}

    public void setAnalyzed_at(LocalDateTime analyzed_at) {this.analyzed_at = analyzed_at;}

    public String getSuggestions() {return suggestions;}

    public void setSuggestions(String suggestions) {this.suggestions = suggestions;}

    public String getIssue() {return issue;}

    public void setIssue(String issue) {this.issue = issue;}

    public String getSeverity() {return severity;}

    public void setSeverity(String severity) {this.severity = severity;}

    public String getClass_name() {return class_name;}

    public void setClass_name(String class_name) {this.class_name = class_name;}

    public File getFile() {return file;}

    public void setFile(File file) {this.file = file;}

    public Session getSession() {return session;}

    public void setSession(Session session) {this.session = session;}

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Result() {}
}
