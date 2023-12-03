package ru.nsu.ccfit.orm.core.meta;

import java.util.List;

/**
 * Интерфейс для взаимодействия с сущностями
 *
 * @param <T>
 */
public interface EntityManager<T> {
    T findById(Class<T> objectClass, Object key);

    List<T> findAll(Class<T> objectClass);

    T create(T object);

    boolean update(T object);

    boolean delete(T object);
}
