package com.app.file.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDeleteMessage {

    private String bucketName;
    private String trashKey;

}
