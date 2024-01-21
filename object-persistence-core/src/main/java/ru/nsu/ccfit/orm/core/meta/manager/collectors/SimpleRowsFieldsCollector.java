package ru.nsu.ccfit.orm.core.meta.manager.collectors;

import java.util.Map;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import static ru.nsu.ccfit.orm.core.utils.FieldUtilsManager.getFieldValue;

public class SimpleRowsFieldsCollector implements ObjectFieldsCollector {
    @Override
    public void collectFields(TableMetaData tableMetaData, Object instance, Map<String, Object> columnsWithValues) {
        tableMetaData.simpleRowsData().forEach((columnName, columnValue) ->
                columnsWithValues.put(columnName, getFieldValue(columnValue, instance))
        );
    }
}
