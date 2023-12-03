package ru.nsu.ccfit.orm.model.meta;

import java.util.Map;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

/**
 * Класс, содержащий метаданные о сущностях
 */
public record TableMetaData(String tableName, IdRowData idRowData, Map<String, FieldInfo> rowsData) {
}
