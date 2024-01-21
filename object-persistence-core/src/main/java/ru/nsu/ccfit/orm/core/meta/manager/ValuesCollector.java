package ru.nsu.ccfit.orm.core.meta.manager;

import java.util.List;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.Map;

public interface ValuesCollector {

    Map<String, Object> collectColumnAndValuesPairs(TableMetaData tableMetaData, Object instance);

    Map<TableMetaData, Object> collectOneToOneValues(TableMetaData tableMetaData, Object instance);

    Map<TableMetaData, List<Object>> collectOneToManyValues(TableMetaData tableMetaData, Object instance);

    Map<TableMetaData, Object> collectManyToOneValues(TableMetaData tableMetaData, Object instance);

}
