package ru.nsu.ccfit.orm.core.meta;

import ru.nsu.ccfit.orm.core.utils.FieldUtilsManager;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultEntityMetaDataManager implements EntityMetaDataManager {
    private final Map<Class<?>, TableMetaData> metaDataTable = new HashMap<>();

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
        String tableName = clazz.getAnnotation(Entity.class).name();
        Map<String, FieldInfo> fieldInfoMap = new LinkedHashMap<>();
        IdRowData idRowData = null;
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = clazz.getDeclaredField(propertyDescriptor.getName());
                    FieldInfo fieldInfo = new FieldInfo(
                            field, propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod()
                    );
                    fieldInfoMap.put(field.getName(), fieldInfo);
                    if (field.getAnnotation(Id.class) != null) {
                        idRowData = new IdRowData(field.getName(), fieldInfo);
                    }
                }
            }
        } catch (NoSuchFieldException | IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
        return new TableMetaData(tableName, idRowData, fieldInfoMap);
    }
}
