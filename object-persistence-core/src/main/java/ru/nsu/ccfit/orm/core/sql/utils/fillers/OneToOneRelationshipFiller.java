package ru.nsu.ccfit.orm.core.sql.utils.fillers;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.orm.core.meta.manager.EntityManager;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

@RequiredArgsConstructor
public class OneToOneRelationshipFiller implements ObjectFiller {
    private final EntityManager entityManager;

    @Override
    public void fillObject(TableMetaData tableMetaData, Object instance, List<String> columnsList, ResultSet rs)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        for (String complexRow : tableMetaData.oneToOneRowsData().keySet()) {
            FieldInfo fieldInfo = tableMetaData.oneToOneRowsData().get(complexRow);
            if (columnsList.contains(complexRow.toLowerCase()) && rs.getObject(complexRow.toLowerCase()) != null) {
                fieldInfo.setter().invoke(instance, entityManager.findById(fieldInfo.getter().getReturnType(),
                        rs.getObject(complexRow.toLowerCase())));
            }
        }
    }
}
