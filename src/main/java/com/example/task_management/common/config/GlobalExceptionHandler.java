package com.example.task_management.common.config;

import com.example.task_management.common.dto.ErrorDetails;
import com.example.task_management.common.exceptions.BadRequestException;
import com.example.task_management.common.exceptions.DuplicateResourceException;
import com.example.task_management.common.exceptions.NotFoundException;
import com.example.task_management.common.exceptions.UnAuthenticatedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleException(NotFoundException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleException(UsernameNotFoundException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnAuthenticatedException.class)
    public ResponseEntity<?> handleException(UnAuthenticatedException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<?> handleException(HttpClientErrorException.Forbidden ex, WebRequest request) {
        return handle(ex, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleException(AuthorizationDeniedException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleException(DuplicateResourceException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleException(MalformedJwtException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleException(BadRequestException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<?> handleException(HttpMessageConversionException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<?> handleException(ServletRequestBindingException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        DefaultMessageSourceResolvable::getDefaultMessage
                ));

        // Detailed error response
        ErrorDetails<String> errorDetails = new ErrorDetails<>(
                LocalDateTime.now(),
                "Validation failed",
                request.getDescription(false),
                errors
        );
        log.error(errorDetails.toString(), ex);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleException(RuntimeException ex, WebRequest request) {
        return handle(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> handle(Exception ex, WebRequest request, HttpStatus httpStatus) {
        ErrorDetails<String> errorDetails = new ErrorDetails<>
                (LocalDateTime.now(), ex.getMessage(), request.getDescription(false), null);
        log.error(errorDetails.toString(), ex);
        return new ResponseEntity<>(errorDetails, httpStatus);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            ErrorDetails<String> errorDetails = new ErrorDetails<>
                    (LocalDateTime.now(), "You are not authorized to access this resource", request.getRequestURI(), null);
            log.error(errorDetails.toString());
            String responseString = objectMapper.writeValueAsString(errorDetails);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(responseString);
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            ErrorDetails<String> errorDetails = new ErrorDetails<>
                    (LocalDateTime.now(), "You need to be authenticated to access this resource", request.getRequestURI(), null);
            log.error(errorDetails.toString());
            String responseString = objectMapper.writeValueAsString(errorDetails);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(responseString);
        };
    }

//    @Bean
//    public ObjectMapper objectMapper(){
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.findAndRegisterModules();
//        return objectMapper;
//    }
}
