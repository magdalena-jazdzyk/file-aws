package com.app.file.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "file")
public class FileEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
    @Column
    private String bucket;
    @Column
    private String s3Key;

    @Column(nullable = true)
    private long fileSize;

    @Column(nullable = true)
    private String mimeType;

    //    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private LocalDate uploadDate;

    @Column
    private String checksum;

    @Lob
    @Column(nullable = true)
    private byte[] fileData;

}
