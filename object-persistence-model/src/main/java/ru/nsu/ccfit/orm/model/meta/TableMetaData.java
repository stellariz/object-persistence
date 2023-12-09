package ru.nsu.ccfit.orm.model.meta;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

/**
 * Класс, содержащий метаданные о сущностях
 */
public record TableMetaData(String tableName,
                            IdRowData idRowData,
                            Map<String, FieldInfo> rowsData,
                            String concatenatedFieldNames,
                            String selectAllFromTable) {
    public TableMetaData(String tableName,
                         IdRowData idRowData,
                         Map<String, FieldInfo> rowsData) {
        this(
                tableName,
                idRowData,
                rowsData,
                String.join(", ", rowsData.keySet()),
                String.join(", ", "SELECT ", " FROM " + tableName)
        );
    }
}
