package com.app.file.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMoveToTrashRequest {

    private String sourceBucket;
    private String destinationBucket;
    private String key;

}
