package com.example.task_management.auth.service;

import com.example.task_management.auth.dto.RoleDto;
import com.example.task_management.auth.entity.Role;
import com.example.task_management.auth.repository.RoleRepository;
import com.example.task_management.common.exceptions.DuplicateResourceException;
import com.example.task_management.common.exceptions.NotFoundException;
import com.example.task_management.common.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role save(RoleDto dto) {
        try {
            return roleRepository.save(EntityUtil.getForCreate(Role.class, dto));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new DuplicateResourceException("Role already exists");
        }
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    public Role findById(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    public void update(long id, RoleDto dto) {
        Role role = findById(id);
        role.update(dto);
        roleRepository.save(role);
    }
}
