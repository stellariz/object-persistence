package ru.nsu.ccfit.orm.core.meta;

import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.orm.core.utils.FieldUtilsManager;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class DefaultEntityMetaDataManager implements EntityMetaDataManager {
    private final Map<Class<?>, TableMetaData> metaDataTable = new HashMap<>();
    private final TableMetaDataBuilder tableMetaDataBuilder = new TableMetaDataBuilder();

    @Override
    public TableMetaData unsafeGetMetaData(Class<?> clazz) {
        if (!metaDataTable.containsKey(clazz)) {
            throw new IllegalArgumentException("There is no metadata for Class: %s".formatted(clazz.getName()));
        }
        return metaDataTable.get(clazz);
    }

    @Override
    public Optional<TableMetaData> getMetaData(Class<?> clazz) {
        if (!FieldUtilsManager.doesExistEntityAnnotation(clazz)) {
            return Optional.empty();
        }

        return Optional.ofNullable(metaDataTable.get(clazz));
    }

    @Override
    public TableMetaData saveMetaData(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Class %s has no @Entity annotation".formatted(clazz.getName()));
        }
        TableMetaData tableMetaData = collectMetaDataFromClass(clazz);
        metaDataTable.put(clazz, tableMetaData);
        return tableMetaData;
    }

    private TableMetaData collectMetaDataFromClass(Class<?> clazz) {
        if (!FieldUtilsManager.isOnlyOneIdField(clazz)) {
            throw new IllegalArgumentException("Entity should have only one @Id field");
        }
        return tableMetaDataBuilder.buildTableMetaDataByClass(clazz);
    }
}
