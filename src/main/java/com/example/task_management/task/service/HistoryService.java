package com.example.task_management.task.service;

import com.example.task_management.common.enums.OperationType;
import com.example.task_management.task.entity.History;
import com.example.task_management.task.entity.Task;
import com.example.task_management.task.repository.HistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    @Transactional
    public History create(Task task, OperationType operation) {
        History history = new History(task, operation);
        return historyRepository.save(history);
    }
}
