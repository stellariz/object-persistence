package ru.nsu.ccfit.orm.core.meta.manager.collectors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.orm.core.meta.manager.EntityMetaDataManager;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

@RequiredArgsConstructor
public class OneToManyRelationshipFieldsCollector implements ObjectFieldsCollector {
    private final EntityMetaDataManager entityMetaDataManager;

    @Override
    public void collectFields(TableMetaData tableMetaData, Object instance, Map<String, Object> columnsWithValues) {

        tableMetaData.oneToManyRowsData().forEach((columnName, columnValue) -> {
                    columnValue.field().setAccessible(true);
                    TableMetaData relatedMetaData = entityMetaDataManager
                            .unsafeGetMetaData(extractParameterizedCollectionClass(columnValue));
                    try {
                        FieldInfo idField = relatedMetaData.idRowData().fieldInfo();

                        List<Object> relatedEntities = (List<Object>) columnValue.field().get(instance);
                        if (relatedEntities != null) {
                            List<Object> idCollections = relatedEntities.stream().map(childInstance -> {
                                        try {
                                            return idField.getter().invoke(childInstance);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            return null;
                                        }

                                    })
                                    .filter(Objects::nonNull).toList();
                            columnsWithValues.put(columnName, idCollections);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
        );
    }

    private Class<?> extractParameterizedCollectionClass(FieldInfo fieldInfo) {
        ParameterizedType collectionParameterizedType = (ParameterizedType) fieldInfo.field().getGenericType();
        Class<?> collectionParameterizedClass = (Class<?>) collectionParameterizedType.getActualTypeArguments()[0];
        return collectionParameterizedClass;
    }
}
