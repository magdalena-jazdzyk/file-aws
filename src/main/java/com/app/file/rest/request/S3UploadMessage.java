package com.app.file.rest.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3UploadMessage {

    private String bucketName;
    private String key;

    private String originalFileName;
    private byte[] fileData;

}
