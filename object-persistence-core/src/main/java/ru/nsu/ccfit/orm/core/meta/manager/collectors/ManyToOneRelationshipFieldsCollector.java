package ru.nsu.ccfit.orm.core.meta.manager.collectors;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.orm.core.meta.manager.EntityMetaDataManager;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

@RequiredArgsConstructor
public class ManyToOneRelationshipFieldsCollector implements ObjectFieldsCollector {
    private final EntityMetaDataManager entityMetaDataManager;
    @Override
    public void collectFields(TableMetaData tableMetaData, Object instance, Map<String, Object> columnsWithValues) {
        tableMetaData.manyToOneRowsData().forEach((columnName, columnValue) -> {
                    TableMetaData relatedMetaData = entityMetaDataManager
                            .unsafeGetMetaData(columnValue.getter().getReturnType());
                    columnValue.field().setAccessible(true);
                    try {
                        Object relatedEntity = columnValue.field().get(instance);
                        if (relatedEntity != null) {
                            FieldInfo idField = relatedMetaData.idRowData().fieldInfo();
                            var relatedEntityId = idField.getter().invoke(relatedEntity);
                            columnsWithValues.put(columnName, relatedEntityId);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                    }
                }
        );

    }
}
