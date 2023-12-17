package ru.nsu.ccfit.orm.core.meta;

import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.Map;

public interface ValuesCollector {

    Map<String, Object> collectColumnAndValuesPairs(TableMetaData tableMetaData, Object instance);

    Map<TableMetaData, Object> collectOneToOneValues(TableMetaData tableMetaData, Object instance);
}
