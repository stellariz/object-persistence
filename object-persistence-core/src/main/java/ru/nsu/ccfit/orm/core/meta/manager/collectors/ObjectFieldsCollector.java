package ru.nsu.ccfit.orm.core.meta.manager.collectors;

import java.util.Map;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

public interface ObjectFieldsCollector {

    void collectFields(TableMetaData tableMetaData, Object instance, Map<String, Object> columnsWithValues);
}
