package ru.nsu.ccfit.orm.model.meta;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

/**
 * Класс, содержащий метаданные о сущностях
 */
public record TableMetaData(AtomicLong counter,
                            String tableName,
                            IdRowData idRowData,
                            Map<String, FieldInfo> allRowsData,
                            Map<String, FieldInfo> simpleRowsData,
                            Map<String, FieldInfo> oneToOneRowsData) {

    public Set<String> getAllRowsName() {
        return allRowsData.keySet();
    }
}
