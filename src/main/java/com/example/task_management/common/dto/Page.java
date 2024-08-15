package com.example.task_management.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class Page<T extends Serializable> implements Serializable {
    private int pageNumber;
    private long totalElements;
    private List<T> data;

    public Page(int pageNumber, long totalElements, List<T> data) {
        this.pageNumber = pageNumber + 1;
        this.totalElements = totalElements;
        this.data = data;
    }
}
