package com.example.task_management.task.entity;

import com.example.task_management.common.entity.BaseEntity;
import com.example.task_management.common.enums.OperationType;
import com.example.task_management.common.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "history")
@AllArgsConstructor
@NoArgsConstructor
public class History extends BaseEntity {

    @Column(name = "task_id", nullable = false)
    private long taskId;
    private String title;
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operation;

    public History(Task task, OperationType operation) {
        this.taskId = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.dueDate = task.getDueDate();
        this.assignedTo = task.getAssignedTo() == null ? null : task.getAssignedTo().getId();
        this.status = task.getStatus();
        this.operation = operation;
    }
}
