package ru.nsu.ccfit.orm.core.sql.utils.fillers;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

public class DefaultTypesFiller implements ObjectFiller {
    @Override
    public void fillObject(TableMetaData tableMetaData, Object instance, List<String> columnsList, ResultSet rs)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        for (String baseRow : tableMetaData.simpleRowsData().keySet()) {
            FieldInfo fieldInfo = tableMetaData.simpleRowsData().get(baseRow);
            if (columnsList.contains(baseRow.toLowerCase())) {
                fieldInfo.setter().invoke(instance, rs.getObject(baseRow.toLowerCase()));
            }
        }
    }
}
