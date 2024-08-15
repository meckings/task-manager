package com.example.task_management.auth.entity;

import com.example.task_management.auth.dto.RoleDto;
import com.example.task_management.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {

    private String name;

    public Role(RoleDto roleDto) {
        update(roleDto);
    }

    public void update(RoleDto dto) {
        this.name = dto.getName();
    }
}
