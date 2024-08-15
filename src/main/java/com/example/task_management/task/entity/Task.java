package com.example.task_management.task.entity;

import com.example.task_management.auth.entity.User;
import com.example.task_management.common.entity.BaseEntity;
import com.example.task_management.common.enums.TaskStatus;
import com.example.task_management.task.dto.TaskDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Task.findAllByStatus", query = "SELECT t FROM Task t WHERE t.status = :status"),
        @NamedQuery(name = "Task.findAllByPriority", query = "SELECT t FROM Task t WHERE t.priority = :priority"),
        @NamedQuery(name = "Task.findAllByDueDate", query = "SELECT t FROM Task t WHERE t.dueDate <= :dueDate")})
public class Task extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false)
    private int priority;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    public Task(TaskDto taskDto) {
        update(taskDto);
    }

    public void update(TaskDto dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.priority = dto.getPriority();
        this.dueDate = dto.getDueDate();
    }
}


