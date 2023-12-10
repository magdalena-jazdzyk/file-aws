package com.app.file.service;

import com.app.file.config.RabbitMQConfig;
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

            rabbitTemplate.convertAndSend(RabbitMQConfig.UPLOAD_QUEUE, new S3UploadMessage(bucketName, key, key + fileExtension, fileData));
            System.out.println("Operacja przesyłania pliku na AWS S3 zostanie przetworzona.");
        } catch (Exception e) {
            System.err.println("Błąd podczas konwersji pliku na tablicę bajtów: " + e.getMessage());
            // Odpowiednia obsługa błędów
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            return ""; // lub domyślne rozszerzenie, jeśli jest znane
        }
    }

    public void deleteFile(FileDeleteMessage request) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.DELETE_QUEUE, request);
        System.out.println("Operacja usuwania pliku zostanie przetworzona.");
    }

    public void moveToTrash(FileMoveToTrashRequest request) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.MOVE_TO_TRASH_QUEUE, request);
    }


    public void restoreFile(FileRestoreRequest request) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RESTORE_QUEUE, request);
    }


    //

    ///
    // ..............
//    public void downloadFile(String bucketName, String key) {
//        FileRequest request = new FileRequest(bucketName, key);
//        rabbitTemplate.convertAndSend(RabbitMQConfig.DOWNLOAD_QUEUE, request);
//        System.out.println("Operacja pobierania pliku zostanie przetworzona.");
//    }

//    W powyższym kodzie FileProducer jest odpowiedzialny za wysyłanie klucza pliku
//            (nazwa pliku) do kolejek RabbitMQ, które są używane do operacji usuwania
//    i pobierania plików. FileConsumer nasłuchuje na odpowiednich kolejkach
//    i wykonuje operacje na plikach w AWS S3 za pomocą S3Service
}
