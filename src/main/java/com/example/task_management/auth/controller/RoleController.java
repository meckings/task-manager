package com.example.task_management.auth.controller;

import com.example.task_management.auth.dto.RoleDto;
import com.example.task_management.auth.entity.Role;
import com.example.task_management.auth.service.RoleService;
import com.example.task_management.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.task_management.common.util.ResponseEntityUtil.validateNumber;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize(SecurityUtil.HAS_ROLE_ADMIN)
    public ResponseEntity<RoleDto> registerUser(@RequestBody @Validated RoleDto role) {
        Role saved = roleService.save(role);
        return new ResponseEntity<>(new RoleDto(saved), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize(SecurityUtil.HAS_ROLE_ADMIN)
    public ResponseEntity<RoleDto> getById(@RequestParam Long id) {
        validateNumber(id, "id cannot be null and should be greater than 0");
        Role saved = roleService.findById(id);
        return new ResponseEntity<>(new RoleDto(saved), HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize(SecurityUtil.HAS_ROLE_ADMIN)
    public ResponseEntity<RoleDto> update(@RequestParam Long id, @Validated @RequestBody RoleDto role) {
        validateNumber(id, "id cannot be null and should be greater than 0");
        roleService.update(id, role);
        return ResponseEntity.noContent().build();
    }
}

