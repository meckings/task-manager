package com.example.task_management.common.util;

import com.example.task_management.auth.dto.CustomUserDetails;
import com.example.task_management.common.enums.Roles;
import com.example.task_management.common.exceptions.UnAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
    public static final String HAS_ROLE_USER = "hasRole('USER')";

    public static CustomUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        throw new UnAuthenticatedException("You need to be authenticate to access this resource");
    }

    public static String hasRole(Roles roles){
        return String.format("hasRole('%s')", roles.name().substring(5));
    }
}
