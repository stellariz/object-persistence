package ru.nsu.ccfit.orm.core.repository;


import java.util.Optional;

/**
 * Интерфейс для взаимодействия пользователя с БД
 *
 * @param <T>
 */
public interface CrudRepository<T, ID> {

    // TODO: 04.12.2023 (r.popov): возвращаемый и принимаемый тип S extends T
    T save(T entity);

    void delete(T entity);

    Optional<T> findById(ID id);
}
