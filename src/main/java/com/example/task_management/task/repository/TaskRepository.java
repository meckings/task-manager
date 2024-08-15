package com.example.task_management.task.repository;

import com.example.task_management.common.enums.TaskStatus;
import com.example.task_management.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t " +
            "WHERE (:status IS NULL OR t.status = :status) " +
            "AND (:dueDate IS NULL OR t.dueDate = :dueDate) " +
            "AND (:title IS NULL OR t.title LIKE %:title%) " +
            "AND (:description IS NULL OR t.description LIKE %:description%)")
    Page<Task> searchTasks(
            @Param("title") String title,
            @Param("description") String description,
            @Param("status") TaskStatus status,
            @Param("dueDate") LocalDate dueDate,
            Pageable pageable
    );

    Page<Task> findByDueDateOrderByCreatedOnDesc(LocalDate now, Pageable pageable);
}
