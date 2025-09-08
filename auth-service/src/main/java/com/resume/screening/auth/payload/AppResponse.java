package com.resume.screening.auth.payload;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppResponse {
    private String message;
    private boolean success;
    private HttpStatus status;
    private LocalDateTime timeStamp;
}
