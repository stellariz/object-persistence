package ru.nsu.ccfit.orm.core.meta.manager;

import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.Optional;

/**
 * Интерфейс для хранения данных о сущностях
 */
public interface EntityMetaDataManager {
    TableMetaData unsafeGetMetaData(Class<?> clazz);

    Optional<TableMetaData> getMetaData(Class<?> clazz);

    TableMetaData saveMetaData(Class<?> clazz);
}
