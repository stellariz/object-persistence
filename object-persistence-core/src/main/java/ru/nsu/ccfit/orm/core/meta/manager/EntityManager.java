package ru.nsu.ccfit.orm.core.meta.manager;

import ru.nsu.ccfit.orm.core.repository.dsl.selector.EntitySelector;

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

    <T> EntitySelector<? extends T> customSearch(Class<? extends T> objectClass);

    void createTableForClass(Class<?> clazz);
}
