package ru.nsu.ccfit.orm.core.meta;

import java.util.Optional;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

/**
 * Интерфейс для хранения данных о сущностях
 */
public interface EntityMetaDataManager {
    TableMetaData unsafeGetMetaData(Class<?> clazz);

    Optional<TableMetaData> getMetaData(Class<?> clazz);

    TableMetaData saveMetaData(Class<?> clazz);
}
