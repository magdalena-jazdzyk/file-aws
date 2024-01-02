package com.app.file.service;

import com.app.file.constant.RabbitQueueConstant;
import com.app.file.rest.request.*;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@AllArgsConstructor
public class FileProducer {

    private final RabbitTemplate rabbitTemplate;

    public void uploadFile(String bucketName, String key, MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            byte[] fileData = file.getBytes();

            rabbitTemplate.convertAndSend(RabbitQueueConstant.UPLOAD_QUEUE, new S3UploadMessage(bucketName, key, key + fileExtension, fileData));
            System.out.println("Operacja przesyłania pliku na AWS S3 zostanie przetworzona.");
        } catch (Exception e) {
            System.err.println("Błąd podczas konwersji pliku na tablicę bajtów: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            return "";
        }
    }

    public void deleteFile(FileDeleteMessage request) {
        rabbitTemplate.convertAndSend(RabbitQueueConstant.DELETE_QUEUE, request);
        System.out.println("Operacja usuwania pliku zostanie przetworzona.");
    }

    public void moveToTrash(FileMoveToTrashRequest request) {
        rabbitTemplate.convertAndSend(RabbitQueueConstant.MOVE_TO_TRASH_QUEUE, request);
    }

    public void restoreFile(FileRestoreRequest request) {
        rabbitTemplate.convertAndSend(RabbitQueueConstant.RESTORE_QUEUE, request);
    }

}
