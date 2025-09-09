package com.resume.screening.entity;

import com.resume.screening.config.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "resumes", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;     //-> This will come from Auth-Service means Foreign Key
    private String fileName;

    private String storagePath;

    @Lob
    private String parsedText;

    private Instant uploadedAt;

    private Instant parsedTime;

    @Enumerated(EnumType.STRING)
    private Status status;

}
