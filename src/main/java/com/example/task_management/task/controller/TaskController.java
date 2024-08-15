package com.example.task_management.task.controller;

import com.example.task_management.common.dto.Page;
import com.example.task_management.common.enums.TaskStatus;
import com.example.task_management.task.dto.TaskDto;
import com.example.task_management.task.entity.Task;
import com.example.task_management.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.example.task_management.common.util.ResponseEntityUtil.validateNumber;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody @Validated TaskDto task) {
        Task saved = taskService.save(task);
        return new ResponseEntity<>(new TaskDto(saved), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<TaskDto> getTask(@RequestParam Long id) {
        validateNumber(id, "id must be greater than 0");
        Task task = taskService.findTaskById(id);
        return new ResponseEntity<>(new TaskDto(task), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> updateTask(@RequestParam Long id, @RequestBody @Validated TaskDto task) {
        validateNumber(id, "id must be greater than 0");
        taskService.updateTask(id, task);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/assign")
    public ResponseEntity<Void> assignTask(@RequestParam Long taskId, @RequestParam Long userId) {
        validateNumber(taskId, "taskId must be greater than 0");
        validateNumber(userId, "userId must be greater than 0");
        taskService.assignTaskToUser(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTask(@RequestParam Long id) {
        validateNumber(id, "id must be greater than 0");
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskDto>> searchTasks(@RequestParam int page,
                                                     @RequestParam int size,
                                                     @RequestParam(required = false) String title,
                                                     @RequestParam(required = false) String description,
                                                     @RequestParam(required = false) TaskStatus status,
                                                     @RequestParam(required = false) LocalDate dueDate) {

        validateNumber((long) page, "page must be greater than 0");
        validateNumber((long) size, "size must be greater than 0");
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TaskDto> tasks = taskService.searchTasks(pageable, title, description, status, dueDate);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
}

