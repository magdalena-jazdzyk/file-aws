package com.app.file.service;


import com.app.file.component.FileComponent;
import com.app.file.mapper.Mapper;
import com.app.file.model.FileEntity;
import com.app.file.model.FilePageRepository;
import com.app.file.model.FileRepository;
import com.app.file.rest.request.DownloadFileRequest;
import com.app.file.rest.request.FileDeleteMessage;
import com.app.file.rest.request.FileMoveToTrashRequest;
import com.app.file.rest.request.FileRestoreRequest;
import com.app.file.rest.response.DownloadFileResponse;
import com.app.file.rest.response.FileResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.app.file.constant.FileConstant.BUCKET_CUSTOMER_IN_CLOUD;
import static com.app.file.constant.FileConstant.BUCKET_TRASH;


@Service
@AllArgsConstructor
public class FileService {

    private final FileComponent fileComponent;
    private final FileProducer fileProducer;
    private final S3Service s3Service;
    private final S3Presigner presigner;
    private final Mapper mapper;
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("pdf", "application/pdf");
    }

    public Page<FileResponse> getAll(Pageable pageable) {
        return fileComponent.findByBucket(BUCKET_CUSTOMER_IN_CLOUD, pageable).map(mapper::from);
    }


    public List<FileResponse> getWithoutPagination() {
        return fileComponent.getAll().stream()
                .map(s -> mapper.from(s))
                .collect(Collectors.toList());
    }

    public void saveFileToDatabase(String bucketName, String s3Key, String fileName) {
        FileEntity fileEntity = FileEntity.builder()
                .bucket(bucketName)
                .name(fileName)
                .s3Key(s3Key)
                .build();
        fileComponent.save(fileEntity);
    }

    public void uploadFile(String bucketName, String key, MultipartFile file) {
        fileProducer.uploadFile(bucketName, key, file);
    }

    public DownloadFileResponse generatePreSignedURL(DownloadFileRequest request) throws FileNotFoundException {
        FileEntity file = fileComponent.findById(request.getFileId());

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(file.getBucket())
                .key(file.getS3Key())
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(b -> b.getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(request.getDurationInMinutes())));

        return new DownloadFileResponse(presignedRequest.url().toString(), file.getName());
    }

    public FileResponse findById(Long id) {
        return mapper.from(fileComponent.findById(id));
    }

    public void deleteFile(FileDeleteMessage request) {
        fileProducer.deleteFile(request);
    }

    public void moveToTrash(FileMoveToTrashRequest request) {
        FileEntity fileEntity = fileComponent.findByS3Key(request.getKey());
        fileEntity.setBucket(BUCKET_TRASH);
        fileComponent.save(fileEntity);
        fileProducer.moveToTrash(request);
    }

    public void configureLifecycleRule(String bucketName) {
        s3Service.configureLifecycleRule(bucketName);
    }

    public void restoreFile(FileRestoreRequest request) {
        fileProducer.restoreFile(request);
    }

}