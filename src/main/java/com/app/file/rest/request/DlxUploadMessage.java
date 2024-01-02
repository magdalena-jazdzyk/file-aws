package com.app.file.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DlxUploadMessage {

    private String bucketName;
    private String key;
    private MultipartFile file;
    private int retryCount;
}
