package com.app.file.service;


import com.app.file.model.FileEntity;
import com.app.file.model.FileRepository;
import com.app.file.rest.request.DownloadFileRequest;
import com.app.file.rest.request.FileDeleteMessage;
import com.app.file.rest.request.FileMoveToTrashRequest;
import com.app.file.rest.request.FileRestoreRequest;
import com.app.file.rest.response.DownloadFileResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Service
@AllArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileProducer fileProducer;
    private final S3Service s3Service;
    private final S3Presigner presigner;
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("pdf", "application/pdf");
        // Dodaj więcej mapowań tutaj
    }

    public void saveFileToDatabase(String bucketName, String s3Key, String fileName) {
        FileEntity fileEntity = FileEntity.builder()
                .bucket(bucketName)
                .name(fileName)
                .s3Key(s3Key)
                .build();
        fileRepository.save(fileEntity);
    }

    public void uploadFile(String bucketName, String key, MultipartFile file) {
        fileProducer.uploadFile(bucketName, key, file);
    }

    public DownloadFileResponse generatePreSignedURL(DownloadFileRequest request) throws FileNotFoundException {
        FileEntity file = findById(request.getFileId());

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(file.getBucket())
                .key(file.getS3Key())
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(b -> b.getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(request.getDurationInMinutes())));

        return new DownloadFileResponse(presignedRequest.url().toString(), file.getName());
    }

    public FileEntity findById(Long id) {
        return fileRepository.findById(id).orElseThrow();
    }

    public void deleteFile(FileDeleteMessage request) {
        //   FileEntity fileEntity = fileRepository.findByS3Key(key).orElseThrow();
        fileProducer.deleteFile(request);
        //   fileRepository.delete(fileEntity);
    }

    public void moveToTrash(FileMoveToTrashRequest request) {
        fileProducer.moveToTrash(request);
    }

    public void configureLifecycleRule(String bucketName) {
        s3Service.configureLifecycleRule(bucketName);
    }

    public void restoreFile(FileRestoreRequest request) {
        fileProducer.restoreFile(request);
    }

}