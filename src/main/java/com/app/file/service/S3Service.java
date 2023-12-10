package com.app.file.service;

import com.app.file.rest.request.FileDeleteMessage;
import com.app.file.rest.request.FileMoveToTrashRequest;
import com.app.file.rest.request.FileRestoreRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


import java.io.IOException;

@Service
@AllArgsConstructor
public class S3Service {

    private final S3Client s3;

    public void putObject(String bucketName, String key, byte[] file) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.putObject(objectRequest, RequestBody.fromBytes(file));
    }

    public byte[] getObject(String bucketName, String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseInputStream<GetObjectResponse> res = s3.getObject(objectRequest);
        try {
            return res.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteObject(FileDeleteMessage request) {
        DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                .bucket(request.getBucketName())
                .key(request.getTrashKey())
                .build();
        s3.deleteObject(objectRequest);
    }

    public void moveObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        // Skopiuj plik do nowej lokalizacji (kosza)
        CopyObjectRequest copyObjRequest = createCopyObjectRequest(sourceBucket, sourceKey, destinationBucket, destinationKey);
        s3.copyObject(copyObjRequest);

        // Usuń oryginalny plik z jego pierwotnej lokalizacji
        DeleteObjectRequest deleteObjRequest = createDeleteObjectRequest(sourceBucket, sourceKey);
        s3.deleteObject(deleteObjRequest);

    }


    public DeleteObjectRequest createDeleteObjectRequest(String bucket, String key) {
        // Użyj wzorca builder do utworzenia DeleteObjectRequest
        return DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
    }

    public CopyObjectRequest createCopyObjectRequest(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        // Utwórz i skonfiguruj obiekt CopyObjectRequest za pomocą wzorca builder
        return CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(sourceKey)
                .destinationBucket(destinationBucket)
                .destinationKey(destinationKey)
                .build();
    }


    public void moveObjectToTrash(FileMoveToTrashRequest request) {
        boolean isFileInTrash = checkIfFileExists(request.getDestinationBucket(), request.getKey());
        if (isFileInTrash) {
            // Logika, jeśli plik już istnieje w koszu
            System.out.println("Plik " + request.getKey() + " już znajduje się w koszu.");
            return;
        }

        s3.copyObject(CopyObjectRequest.builder()
                .sourceBucket(request.getSourceBucket())
                .sourceKey(request.getKey())
                .destinationBucket(request.getDestinationBucket())
                .destinationKey(request.getKey())
                .build());
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(request.getSourceBucket())
                .key(request.getKey())
                .build());
    }

    public void configureLifecycleRule(String bucketName) {
        LifecycleRule rule = LifecycleRule.builder()
                .id("MoveToTrashAndDeleteAfter30Days")
                .filter(LifecycleRuleFilter.builder()
                        .prefix("trash/") // Filtr dla obiektów z prefiksem 'trash/'
                        .build())
                .status(ExpirationStatus.ENABLED)
                .expiration(LifecycleExpiration.builder()
                        .days(30) // Usuń po 30 dniach
                        .build())
                .build();

        PutBucketLifecycleConfigurationRequest request = PutBucketLifecycleConfigurationRequest.builder()
                .bucket(bucketName)
                .lifecycleConfiguration(BucketLifecycleConfiguration.builder()
                        .rules(rule)
                        .build())
                .build();

        s3.putBucketLifecycleConfiguration(request);
    }

    public void restoreFileFromTrash(FileRestoreRequest request) {
        //try {
            // Tworzenie nowego klucza w kubełku docelowym, jeśli potrzebne

            // Kopiowanie pliku z kubełka trash do file_customer
            s3.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(request.getSourceBucket())
                    .sourceKey(request.getKey())
                    .destinationBucket(request.getDestinationBucket())
                    .destinationKey(request.getKey())
                    .build());
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(request.getSourceBucket())
                    .key(request.getKey())
                    .build());

            System.out.println("Plik przywrócony pomyślnie: " + request.getKey());

//        } catch (S3Exception e) {
//            System.err.println(e.awsErrorDetails().errorMessage());
//            throw e;
//        }
    }


    private boolean checkIfFileExists(String sourceBucket, String objectKey) {
        try {
            // Sprawdzenie, czy plik istnieje poprzez próbę pobrania jego metadanych
            s3.headObject(HeadObjectRequest.builder()
                    .bucket(sourceBucket)
                    .key(objectKey)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}
