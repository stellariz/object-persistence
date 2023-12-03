package ru.nsu.ccfit.orm.core.meta;

import java.util.Optional;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

/**
 * Интерфейс для хранения данных о сущностях
 *
 * @param <T>
 */
public interface EntityMetaDataManager<T> {
    TableMetaData unsafeGetMetaData(Class<T> clazz);

    Optional<TableMetaData> getMetaData(Class<?> clazz);

    TableMetaData saveMetaData(Class<T> clazz);
}
