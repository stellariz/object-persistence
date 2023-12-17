package ru.nsu.ccfit.orm.core.meta.manager;

import java.util.List;

/**
 * Интерфейс для взаимодействия с сущностями
 *
 * @param <T>
 */
public interface EntityManager {
    <T> T findById(Class<? extends T> objectClass, Object key);

    <T> List<T> findAll(Class<? extends T> objectClass);

    <T> T create(T object);

    <T> T update(T object);

    <T> boolean delete(T object);

    void createTableForClass(Class<?> clazz);
}
