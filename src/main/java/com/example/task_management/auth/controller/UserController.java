package com.example.task_management.auth.controller;

import com.example.task_management.auth.dto.UserDto;
import com.example.task_management.auth.entity.User;
import com.example.task_management.auth.service.UserService;
import com.example.task_management.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.task_management.common.util.ResponseEntityUtil.validateNumber;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Validated UserDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userService.save(user);
        return new ResponseEntity<>(new UserDto(saved), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<UserDto> getById(@RequestParam Long id) {
        validateNumber(id, "id cannot be null and should be greater than 0");
        User saved = userService.findById(id);
        return new ResponseEntity<>(new UserDto(saved), HttpStatus.OK);
    }

    @PutMapping("/role")
    @PreAuthorize(SecurityUtil.HAS_ROLE_ADMIN)
    public ResponseEntity<Void> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        validateNumber(userId, "userId cannot be null and must be greater than 0");
        validateNumber(roleId, "roleId cannot be null and must be greater than 0");
        userService.assignRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}

