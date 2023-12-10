package com.app.file.rest;

import com.app.file.rest.request.DownloadFileRequest;
import com.app.file.rest.request.FileDeleteMessage;
import com.app.file.rest.request.FileMoveToTrashRequest;
import com.app.file.rest.request.FileRestoreRequest;
import com.app.file.rest.response.DownloadFileResponse;
import com.app.file.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("api/v1/file")
@AllArgsConstructor
public class FileController {


    //    private final FileProducer fileProducer;
    private final FileService fileService;

    @PostMapping()
    public String uploadFileToS3(@RequestParam("bucketName") String bucketName, @RequestParam("key") String key,
                                 @RequestParam("file") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                fileService.uploadFile(bucketName, key, file);
                return "Plik zostanie przesłany na AWS S3.";
            } else {
                return "Błąd: Plik jest pusty.";
            }
        } catch (Exception e) {
            return "Błąd: " + e.getMessage();
        }
    }

    @DeleteMapping()
    public void deleteFile(@RequestBody FileDeleteMessage request) {
        fileService.deleteFile(request);
    }

    @GetMapping("/files/download")
    public ResponseEntity<DownloadFileResponse> generateDownloadLink(@RequestBody DownloadFileRequest request) {
        try {
            DownloadFileResponse response = fileService.generatePreSignedURL(request);
            return ResponseEntity.ok(response);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/move-to-trash")
    public String moveToTrash(@RequestBody FileMoveToTrashRequest request) {
        fileService.moveToTrash(request);
        return "Żądanie przeniesienia do kosza zostało wysłane.";
    }

    @PostMapping("/configure-lifecycle")
    public String configureLifecycleRule(@RequestParam String bucketName) {
        fileService.configureLifecycleRule(bucketName);
        return "Zasady cyklu życia zostały skonfigurowane.";
    }

    @PostMapping("/restore")
    public String restoreFile(@RequestBody FileRestoreRequest request) {
        fileService.restoreFile(request);
        return "Żądanie odzyskania pliku z kosza zostało wysłane.";
    }


}

//
//    Kontroler obsługuje żądania użytkownika, odbiera dane pliku i przekazuje je do producenta.
//        Producent konwertuje te dane na wiadomość i wysyła ją do kolejki RabbitMQ.
//        Konsument odbiera wiadomość z kolejki RabbitMQ i przetwarza ją, wykonując operację przesyłania pliku na AWS S3.