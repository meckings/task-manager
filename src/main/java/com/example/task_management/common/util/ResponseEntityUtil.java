package com.example.task_management.common.util;

import com.example.task_management.common.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
public class ResponseEntityUtil {

    public static URI createResponseUri(long id) {
        String uri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return UriComponentsBuilder.fromHttpUrl(uri).build("id", id);
    }

    public static void validateNumber(Long num, String message) {
        if (num == null || num < 1) {
            throw new BadRequestException(message);
        }
    }
}
