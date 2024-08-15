package com.example.task_management.auth.dto;

import com.example.task_management.auth.entity.Role;
import com.example.task_management.common.dto.BaseDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto extends BaseDto {
    @NotEmpty(message = "name cannot be empty")
    private String name;

    public RoleDto(Role role) {
        this.name = role.getName();
        this.setId(role.getId());
        this.setCreatedOn(role.getCreatedOn());
        this.setUpdatedOn(role.getUpdatedOn());
    }
}
