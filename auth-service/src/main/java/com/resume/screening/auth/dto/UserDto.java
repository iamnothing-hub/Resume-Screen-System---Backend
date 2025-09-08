package com.resume.screening.auth.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserDto {
    private Long id;

    private String name;
    private String email;
    private String password;
    private String role;
}
