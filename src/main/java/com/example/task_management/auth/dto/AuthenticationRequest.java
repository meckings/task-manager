package com.example.task_management.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotEmpty(message = "email cannot be empty")
    @Email(message = "use a valid email address")
    private String email;

    @NotEmpty(message = "password cannot be empty")
    private String password;
}
