package org.example.githubfiles.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;



@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "repository_id",nullable = false)
    private Repository repository;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false,name = "sha_code")
    private String hash;

    @Column(nullable = false)
    private boolean isActive = true;

    public boolean isActive() {return isActive;}

    public void setActive(boolean active) {isActive = active;}

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getHash() {return hash;}

    public void setHash(String hash) {this.hash = hash;}

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public String getContent() {return content;}

    public void setContent(String content) {this.content = content;}

    public String getPath() {return path;}

    public void setPath(String path) {this.path = path;}

    public Repository getRepository() {return repository;}

    public void setRepository(Repository repository) {this.repository = repository;}

    public File() {
    }

}
