package com.app.file.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponse {

    private Long id;

    private String name;

    private String bucket;

    private String s3Key;

    private long fileSize;

    private String mimeType;

    private LocalDate uploadDate;

    private String checksum;

    private byte[] fileData;
}
