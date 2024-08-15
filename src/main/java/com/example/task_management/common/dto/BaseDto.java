package com.example.task_management.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDto implements Serializable {
    private Long id;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}

