package ru.nsu.ccfit.orm.core.meta;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.orm.core.meta.manager.EntityMetaDataManager;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static ru.nsu.ccfit.orm.core.utils.FieldUtilsManager.getFieldValue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public class BasicValuesCollector implements ValuesCollector {
    private final EntityMetaDataManager entityMetaDataManager;

    public Map<String, Object> collectColumnAndValuesPairs(TableMetaData tableMetaData, Object instance) {
        // !!! dependency on order !!!
        Map<String, Object> columnsWithValues = new LinkedHashMap<>();

        Optional.ofNullable(getFieldValue(tableMetaData.idRowData().fieldInfo(), instance))
                .ifPresentOrElse(value -> {
                            columnsWithValues.put(tableMetaData.idRowData().idFieldName(), value);
                            tableMetaData.counter().set((Long) value);
                        },
                        () -> columnsWithValues.put(
                                tableMetaData.idRowData().idFieldName(),
                                tableMetaData.counter().incrementAndGet()
                        )
                );

        tableMetaData.simpleRowsData().forEach((columnName, columnValue) ->
                columnsWithValues.put(columnName, getFieldValue(columnValue, instance))
        );

        tableMetaData.oneToOneRowsData().forEach((columnName, columnValue) -> {
                    columnValue.field().setAccessible(true);
                    try {
                        if (columnValue.field().get(instance) != null) {
                            var relatedEntityId =
                                    entityMetaDataManager.unsafeGetMetaData(columnValue.getter().getReturnType())
                                            .counter()
                                            .longValue();
                            columnsWithValues.put(columnName, relatedEntityId);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
        );

        return columnsWithValues;
    }

    public Map<TableMetaData, Object> collectOneToOneValues(TableMetaData tableMetaData, Object instance) {
        Map<TableMetaData, Object> map = new HashMap<>();

        tableMetaData.oneToOneRowsData().values().forEach(
                fieldInfo -> map.put(
                        entityMetaDataManager.unsafeGetMetaData(fieldInfo.getter().getReturnType()),
                        getFieldValue(fieldInfo, instance)
                ));

        return map;
    }
}
