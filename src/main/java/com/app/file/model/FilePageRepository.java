package com.app.file.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface FilePageRepository extends PagingAndSortingRepository<FileEntity, Long> {

    @Query("SELECT f FROM FileEntity f WHERE f.bucket = :bucket")
    Page<FileEntity> findByBucket(@Param("bucket") String bucket, Pageable pageable);

}
