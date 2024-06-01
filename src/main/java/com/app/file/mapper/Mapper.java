package com.app.file.mapper;

import com.app.file.model.FileEntity;
import com.app.file.rest.response.FileResponse;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public FileResponse from(FileEntity entity) {
        return FileResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .bucket(entity.getBucket())
                .s3Key(entity.getS3Key())
                .fileSize(entity.getFileSize())
                .mimeType(entity.getMimeType())
                .uploadDate(entity.getUploadDate())
                .checksum(entity.getChecksum())
                .build();
    }
}
