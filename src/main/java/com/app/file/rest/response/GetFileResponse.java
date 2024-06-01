package com.app.file.rest.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetFileResponse {


    private Long id;

    private String name;

    private long fileSize;

    private String mimeType;

    private LocalDate uploadDate;

}
