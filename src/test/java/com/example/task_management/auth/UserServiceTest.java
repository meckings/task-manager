package com.example.task_management.auth;

import com.example.task_management.auth.dto.UserDto;
import com.example.task_management.auth.entity.Role;
import com.example.task_management.auth.entity.User;
import com.example.task_management.auth.repository.UserRepository;
import com.example.task_management.auth.service.RoleService;
import com.example.task_management.auth.service.UserService;
import com.example.task_management.common.enums.Roles;
import com.example.task_management.common.exceptions.DuplicateResourceException;
import com.example.task_management.common.util.EntityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles(List.of(new Role(Roles.ROLE_USER.name())));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("test@example.com"));
    }

    @Test
    void saveUser_ShouldSaveUser() {
        // Given
        UserDto userDto = new UserDto();
        Role role = new Role(Roles.ROLE_USER.name());
        User user = new User();
        when(roleService.findByName(Roles.ROLE_USER.name())).thenReturn(role);
        when(userRepository.save(any())).thenReturn(user);

        // When
        User result = userService.save(userDto);

        // Then
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void saveUser_ShouldThrowDuplicateResourceException() {
        // Given
        UserDto userDto = new UserDto();
        Role role = new Role(Roles.ROLE_USER.name());
        User user = new User();
        when(roleService.findByName(Roles.ROLE_USER.name())).thenReturn(role);
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> userService.save(userDto));
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        // Given
        User user = new User();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        User result = userService.findByEmail("test@example.com");

        // Then
        assertEquals(user, result);
    }

    @Test
    void findByEmail_ShouldThrowUsernameNotFoundException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.findByEmail("test@example.com"));
    }

    @Test
    void findById_ShouldReturnUser() {
        // Given
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.findById(1L);

        // Then
        assertEquals(user, result);
    }

    @Test
    void findById_ShouldThrowUsernameNotFoundException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void assignRole_ShouldAssignRoleToUser() {
        // Given
        User user = new User();
        user.setRoles(new ArrayList<>());
        Role role = new Role(Roles.ROLE_USER.name());

        when(roleService.findById(1L)).thenReturn(role);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // When
        userService.assignRole(1L, 1L);

        // Then
        assertTrue(user.getRoles().contains(role));
        verify(userRepository).save(user);
    }
}

