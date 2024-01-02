package com.app.file.service;

import com.app.file.constant.RabbitQueueConstant;
import com.app.file.rest.request.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class FileConsumer {

    private final S3Service s3Service;

    private final FileService fileService;

    @RabbitListener(queues = RabbitQueueConstant.UPLOAD_QUEUE)
    public void uploadFileToS3(S3UploadMessage request) {
        try {
            s3Service.putObject(request.getBucketName(), request.getKey(), request.getFileData());
            fileService.saveFileToDatabase(request.getBucketName(), request.getKey(), request.getOriginalFileName());

            System.out.println("Przesłano plik na AWS S3: " + request.getKey());
        } catch (Exception e) {
            System.err.println("Błąd podczas przesyłania pliku na AWS S3: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitQueueConstant.DELETE_QUEUE)
    public void deleteFileFromS3(FileDeleteMessage request) {
        try {
            s3Service.deleteObject(request);
            System.out.println("Usunięto plik z AWS S3: " + request.getTrashKey());
        } catch (Exception e) {
            System.err.println("Błąd podczas usuwania pliku z AWS S3: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitQueueConstant.MOVE_TO_TRASH_QUEUE)
    public void handleTrashMessage(FileMoveToTrashRequest request) {
        s3Service.moveObjectToTrash(request);
    }

    @RabbitListener(queues = RabbitQueueConstant.RESTORE_QUEUE)
    public void restoreFileFromTrash(FileRestoreRequest request) {
        s3Service.restoreFileFromTrash(request);
    }
}
