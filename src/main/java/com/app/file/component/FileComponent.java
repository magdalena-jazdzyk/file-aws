package com.app.file.component;

import com.app.file.model.FileEntity;
import com.app.file.model.FilePageRepository;
import com.app.file.model.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class FileComponent {

    private final FileRepository fileRepository;
    private final FilePageRepository filePageRepository;

    public List<FileEntity> getAll() {
        return fileRepository.findAll();
    }

    public Page<FileEntity> findByBucket(String bucket, Pageable pageable) {
        return filePageRepository.findByBucket(bucket, pageable);
    }

    public FileEntity findById(Long id) {
        return fileRepository.findById(id).orElseThrow();
    }

    public void deleteFileById(Long id) {
        fileRepository.deleteById(id);
    }

    public FileEntity findByS3Key(String key) {
        return fileRepository.findByS3Key(key).orElseThrow(() -> new RuntimeException("Plik o podanym kluczu nie istnieje"));
    }

    public FileEntity save(FileEntity file) {
        return fileRepository.save(file);
    }


}
