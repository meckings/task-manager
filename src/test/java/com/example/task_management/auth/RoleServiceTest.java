package com.example.task_management.auth;

import com.example.task_management.auth.dto.RoleDto;
import com.example.task_management.auth.entity.Role;
import com.example.task_management.auth.repository.RoleRepository;
import com.example.task_management.auth.service.RoleService;
import com.example.task_management.common.enums.Roles;
import com.example.task_management.common.exceptions.DuplicateResourceException;
import com.example.task_management.common.exceptions.NotFoundException;
import com.example.task_management.common.util.EntityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void saveRole_ShouldSaveRole() {
        // Given
        RoleDto roleDto = new RoleDto();
        Role role = new Role();
        when(roleRepository.save(any())).thenReturn(role);

        // When
        Role result = roleService.save(roleDto);

        // Then
        assertNotNull(result);
        assertEquals(role, result);
    }

    @Test
    void saveRole_ShouldThrowDuplicateResourceException() {
        // Given
        RoleDto roleDto = new RoleDto();
        when(roleRepository.save(any())).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> roleService.save(roleDto));
    }

    @Test
    void findByName_ShouldReturnRole() {
        // Given
        Role role = new Role();
        when(roleRepository.findByName(Roles.ROLE_USER.name())).thenReturn(Optional.of(role));

        // When
        Role result = roleService.findByName(Roles.ROLE_USER.name());

        // Then
        assertEquals(role, result);
    }

    @Test
    void findByName_ShouldThrowNotFoundException() {
        // Given
        when(roleRepository.findByName(Roles.ROLE_USER.name())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> roleService.findByName(Roles.ROLE_USER.name()));
    }

    @Test
    void findById_ShouldReturnRole() {
        // Given
        Role role = new Role();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // When
        Role result = roleService.findById(1L);

        // Then
        assertEquals(role, result);
    }

    @Test
    void findById_ShouldThrowNotFoundException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> roleService.findById(1L));
    }

    @Test
    void updateRole_ShouldUpdateRole() {
        // Given
        RoleDto roleDto = new RoleDto();
        Role role = new Role();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        // When
        roleService.update(1L, roleDto);

        // Then
        verify(roleRepository).save(role);
    }
}

