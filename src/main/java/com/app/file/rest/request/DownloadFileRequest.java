package com.app.file.rest.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFileRequest {
   private Long fileId;
   private int durationInMinutes;

}
