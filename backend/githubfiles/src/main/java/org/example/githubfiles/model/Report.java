package org.example.githubfiles.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "analysis_session_id",nullable = false)
    private Session session;

    @Column(nullable = false,name = "generated_at")
    private LocalDateTime created_at;

    @Column(nullable = false,name = "file_path")
    private String path;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getPath() {return path;}

    public void setPath(String path) {this.path = path;}

    public LocalDateTime getCreated_at() {return created_at;}

    public void setCreated_at(LocalDateTime created_at) {this.created_at = created_at;}

    public Session getSession() {return session;}

    public void setSession(Session session) {this.session = session;}

    public Report() {}
}
