package com.example.task_management.task;

import com.example.task_management.auth.dto.CustomUserDetails;
import com.example.task_management.auth.entity.User;
import com.example.task_management.auth.service.UserService;
import com.example.task_management.common.dto.Page;
import com.example.task_management.common.enums.OperationType;
import com.example.task_management.common.exceptions.NotFoundException;
import com.example.task_management.common.util.EntityUtil;
import com.example.task_management.common.util.SecurityUtil;
import com.example.task_management.notification.service.NotificationService;
import com.example.task_management.task.dto.TaskDto;
import com.example.task_management.task.entity.Task;
import com.example.task_management.task.repository.TaskRepository;
import com.example.task_management.task.service.HistoryService;
import com.example.task_management.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private HistoryService historyService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void init(){
        CustomUserDetails customUserDetails = new CustomUserDetails("test@example.com", "password", List.of());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void saveTask_ShouldSaveTask() {
        // Given
        TaskDto taskDto = new TaskDto();
        taskDto.setAssignedTo(1L);

        User createdBy = new User();
        createdBy.setId(1L);
        User assignedTo = new User();
        assignedTo.setId(2L);
        Task task = new Task();
        task.setId(1L);

        when(userService.findByEmail("test@example.com")).thenReturn(createdBy);
        when(userService.findById(1L)).thenReturn(assignedTo);
        when(taskRepository.save(any())).thenReturn(task);

        // When
        Task task1 = taskService.save(taskDto);
        assertEquals(task1, task);

        // Then
        verify(historyService).create(task, OperationType.CREATE);
        verify(notificationService).sendNotification(task, assignedTo, "New Task!", "You have a new task assigned to you");
    }

    @Test
    void findTaskById_ShouldReturnTask() {
        // Given
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // When
        Task result = taskService.findTaskById(1L);

        // Then
        assertEquals(task, result);
    }

    @Test
    void findTaskById_ShouldThrowNotFoundException() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> taskService.findTaskById(1L));
    }

    @Test
    void updateTask_ShouldUpdateTask() {
        // Given
        TaskDto taskDto = new TaskDto();
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // When
        taskService.updateTask(1L, taskDto);

        // Then
        verify(taskRepository).save(task);
        verify(historyService).create(task, OperationType.UPDATE_TASK_INFO);
    }

    @Test
    void assignTaskToUser_ShouldAssignTask() {
        // Given
        Task task = new Task();
        User user = new User();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.findById(2L)).thenReturn(user);

        // When
        taskService.assignTaskToUser(1L, 2L);

        // Then
        verify(taskRepository).save(task);
        verify(historyService).create(task, OperationType.UPDATE_ASSIGNEE);
        verify(notificationService).sendNotification(task, user, "New Task!", "You have a new task assigned to you");
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        // Given
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository).delete(task);
    }

    @Test
    void searchTasks_ShouldReturnTasksPage() {
        // Given
        Task task = new Task();
        User user = new User();
        user.setId(1L);
        task.setCreatedBy(user);
        Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Task> taskPage = new org.springframework.data.domain.PageImpl<>(List.of(task), pageable, 1);
        when(taskRepository.searchTasks(null, null, null, null, pageable)).thenReturn(taskPage);

        // When
        Page<TaskDto> result = taskService.searchTasks(pageable, null, null, null, null);

        // Then
        assertEquals(1, result.getTotalElements());
    }
}
