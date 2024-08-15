package com.example.task_management.auth.dto;

import com.example.task_management.auth.entity.User;
import com.example.task_management.common.dto.BaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDto extends BaseDto {

    @NotEmpty(message = "email cannot be empty")
    @Email(message = "use a valid email address")
    private String email;

    @NotEmpty(message = "password cannot be empty")
    private String password;

    @JsonIgnore
    private Set<RoleDto> roles;

    public UserDto(User user) {
        this.email = user.getEmail();
        this.roles = user.getRoles()
                .stream()
                .map(RoleDto::new)
                .collect(Collectors.toSet());
        this.setId(user.getId());
        this.setCreatedOn(user.getCreatedOn());
        this.setUpdatedOn(user.getUpdatedOn());
    }
}
