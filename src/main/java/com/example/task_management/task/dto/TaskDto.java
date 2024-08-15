package com.example.task_management.task.dto;

import com.example.task_management.common.dto.BaseDto;
import com.example.task_management.common.enums.TaskStatus;
import com.example.task_management.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto extends BaseDto {

    @NotEmpty(message = "Title cannot be empty")
    @Size(max = 255, message = "Title cannot be longer than 255 characters")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 2000, message = "Description cannot be longer than 2000 characters")
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TaskStatus status;

    @Min(value = 0, message = "priority must be greater than or equal to 0")
    private int priority;

    @NotNull(message = "dueDate cannot be null")
    private LocalDate dueDate;
    private Long assignedTo;
    private Long createdBy;

    public TaskDto(Task task) {
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.dueDate = task.getDueDate();
        this.createdBy = task.getCreatedBy().getId();
        this.assignedTo = ObjectUtils.isEmpty(task.getAssignedTo()) ? null : task.getAssignedTo().getId();
        this.setId(task.getId());
        this.setCreatedOn(task.getCreatedOn());
        this.setUpdatedOn(task.getUpdatedOn());
    }
}
