package com.example.task_management.common.util;

import com.example.task_management.common.dto.BaseDto;
import com.example.task_management.common.entity.BaseEntity;

import java.lang.reflect.InvocationTargetException;

public class EntityUtil {

    public static <S extends BaseEntity, T extends BaseDto> S getForCreate(Class<S> entityClass, T dto) {
        try {
            return entityClass.getConstructor(dto.getClass()).newInstance(dto);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <S extends BaseEntity, T extends BaseDto> S getForUpdate(Class<S> entityClass, T dto) {
        try {
            S entity = entityClass.getConstructor(dto.getClass()).newInstance(dto);
            entity.setId(dto.getId());
            return entity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
