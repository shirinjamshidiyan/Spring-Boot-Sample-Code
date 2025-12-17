package com.shijam.jpajoinentitypattern.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private ErrorCode code;
    private String path;
    private List<String> details;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeStamp;

    public static ErrorResponse of(
            String message,
            ErrorCode code,
            String path,
            List<String> details
    ) {
        return new ErrorResponse( message, code, path, details, LocalDateTime.now());
    }

}
