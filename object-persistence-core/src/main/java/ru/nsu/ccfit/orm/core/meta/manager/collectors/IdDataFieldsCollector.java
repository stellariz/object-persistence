package ru.nsu.ccfit.orm.core.meta.manager.collectors;

import java.util.Map;
import java.util.Optional;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import static ru.nsu.ccfit.orm.core.utils.FieldUtilsManager.getFieldValue;

public class IdDataFieldsCollector implements ObjectFieldsCollector {

    @Override
    public void collectFields(TableMetaData tableMetaData, Object instance, Map<String, Object> columnsWithValues ) {
        Optional.ofNullable(getFieldValue(tableMetaData.idRowData().fieldInfo(), instance))
                // trying to create generator id
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
    }
}
