package ru.nsu.ccfit.orm.core.sql.utils.fillers;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

public interface ObjectFiller {
    void fillObject(TableMetaData tableMetaData, Object instance, List<String> columnsList, ResultSet rs)
            throws SQLException, InvocationTargetException, IllegalAccessException;
}
