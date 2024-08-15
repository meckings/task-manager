package com.example.task_management.task.service;

import com.example.task_management.auth.dto.CustomUserDetails;
import com.example.task_management.auth.entity.User;
import com.example.task_management.auth.service.UserService;
import com.example.task_management.common.dto.Page;
import com.example.task_management.common.enums.OperationType;
import com.example.task_management.common.enums.TaskStatus;
import com.example.task_management.common.exceptions.NotFoundException;
import com.example.task_management.common.util.EntityUtil;
import com.example.task_management.common.util.SecurityUtil;
import com.example.task_management.notification.service.NotificationService;
import com.example.task_management.task.dto.TaskDto;
import com.example.task_management.task.entity.Task;
import com.example.task_management.task.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final HistoryService historyService;
    private final NotificationService notificationService;

    @Value("${tasks.job.batch-size}")
    private int batchSize;

    @Transactional
    public Task save(TaskDto dto) {
        User createdBy = getUser();
        User assignedTo = null;
        if (dto.getAssignedTo() != null && dto.getAssignedTo() > 0) {
            assignedTo = userService.findById(dto.getAssignedTo());
        }

        Task task = EntityUtil.getForCreate(Task.class, dto);
        task.setAssignedTo(assignedTo);
        task.setCreatedBy(createdBy);
        task.setStatus(TaskStatus.TODO);
        task = taskRepository.save(task);
        historyService.create(task, OperationType.CREATE);
        sendNewTaskNotification(task, assignedTo);
        return task;
    }

    public Task findTaskById(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    @Transactional
    public void updateTask(long id, TaskDto dto) {
        Task task = findTaskById(id);
        task.update(dto);
        taskRepository.save(task);
        historyService.create(task, OperationType.UPDATE_TASK_INFO);
    }

    @Transactional
    public void assignTaskToUser(long taskId, long userId) {
        Task task = findTaskById(taskId);
        User user = userService.findById(userId);
        if (task.getAssignedTo() == null || ObjectUtils.notEqual(task.getAssignedTo().getId(), user.getId())) {
            task.setAssignedTo(user);
            taskRepository.save(task);
            historyService.create(task, OperationType.UPDATE_ASSIGNEE);
            sendNewTaskNotification(task, user);
        }
    }

    @Transactional
    public void deleteTask(long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    public Page<TaskDto> searchTasks(Pageable pageable,
                                     String title,
                                     String description,
                                     TaskStatus status,
                                     LocalDate dueDate) {
        org.springframework.data.domain.Page<Task> tasks = taskRepository
                .searchTasks(title, description, status, dueDate, pageable);

        List<TaskDto> taskDtos = tasks.stream().map(TaskDto::new).toList();
        log.info(tasks.toString());
        return new Page<>(tasks.getNumber(), tasks.getTotalElements(), taskDtos);
    }

    @Transactional
    @Scheduled(fixedDelayString = "${tasks.job.interval}", timeUnit = TimeUnit.HOURS)
    public void checkDueTasks() {
        taskRepository.findByDueDateOrderByCreatedOnDesc(LocalDate.now(), Pageable.ofSize(batchSize))
                .forEach(task -> {
                    log.info("Notifying user for due tasks : {}", task);
                    sendDueDateNotification(task, task.getAssignedTo());
                });

        int page = 0;
        boolean hasMorePages = true;

        while (hasMorePages) {
            Pageable pageable = PageRequest.of(page, batchSize);
            org.springframework.data.domain.Page<Task> tasksPage = taskRepository.findByDueDateOrderByCreatedOnDesc(LocalDate.now(), pageable);
            tasksPage.forEach(task -> sendDueDateNotification(task, task.getAssignedTo()));
            page++;
            hasMorePages = tasksPage.hasNext();
        }
    }

    private User getUser() {
        CustomUserDetails userDetails = SecurityUtil.getUserDetails();
        return userService.findByEmail(userDetails.getUsername());
    }

    private void sendNewTaskNotification(Task task, User user) {
        if (user != null) {
            notificationService.sendNotification(task, user, "New Task!", "You have a new task assigned to you");
        }
    }

    private void sendDueDateNotification(Task task, User user) {
        if (user != null) {
            notificationService.sendNotification(task, user, "Due date is here!", "Some of the tasks assigned to you are due today.");
        }
    }
}
