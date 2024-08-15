package com.example.task_management.common.util;

import org.apache.commons.lang3.ObjectUtils;

public class CompareUtil {

    public static <T extends Comparable<T>> T getChanged(T old, T newT) {
        if (ObjectUtils.compare(old, newT) == 0) {
            return null;
        }
        return old;
    }
}
