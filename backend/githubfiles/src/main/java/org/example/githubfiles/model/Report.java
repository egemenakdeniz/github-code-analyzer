package org.example.githubfiles.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "analysis_session_id",nullable = false)
    private Session session;

    @Column(nullable = false,name = "generated_at",columnDefinition = "timestamp without time zone")
    private LocalDateTime created_at;

    public byte[] getFileData() {
        return fileData;
    }

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "file_data", nullable = false, columnDefinition = "BYTEA") // PostgreSQL için , columnDefinition = "bytea"
    private byte[] fileData;



}
