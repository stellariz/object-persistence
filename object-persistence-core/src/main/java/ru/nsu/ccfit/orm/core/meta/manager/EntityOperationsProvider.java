package ru.nsu.ccfit.orm.core.meta.manager;

import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.List;

public interface EntityOperationsProvider {

    <T> T create(TableMetaData tableMetaData, Object object);

    boolean delete(TableMetaData tableMetaData, Object object);

    <T> T update(TableMetaData tableMetaData, Object object);

    <T> T findById(TableMetaData tableMetaData, Object key, Class<?> objectClass);

    <T> List<T> findByAll(TableMetaData tableMetaData, Class<?> objectClass);

}
