package ru.nsu.ccfit.orm.core.sql.utils.fillers;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

public class IdDataFiller implements ObjectFiller {
    @Override
    public void fillObject(TableMetaData tableMetaData, Object instance, List<String> columnsList, ResultSet rs)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        final var idRowData = tableMetaData.idRowData();
        if (columnsList.contains(idRowData.idFieldName().toLowerCase())) {
            idRowData.fieldInfo().setter().invoke(instance, rs.getObject(idRowData.idFieldName().toLowerCase()));
        }
    }
}
