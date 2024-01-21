package ru.nsu.ccfit.orm.core.sql.utils.fillers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.orm.core.meta.manager.EntityManager;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

@RequiredArgsConstructor
public class OneToManyRelationshipFiller implements ObjectFiller {
    private final EntityManager entityManager;

    @Override
    public void fillObject(TableMetaData tableMetaData, Object instance, List<String> columnsList, ResultSet rs)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        for (String complexRow : tableMetaData.oneToManyRowsData().keySet()) {
            FieldInfo fieldInfo = tableMetaData.oneToManyRowsData().get(complexRow);
            Class<?> relatedClass = extractParameterizedCollectionClass(fieldInfo);

            if (columnsList.contains(complexRow.toLowerCase()) && rs.getObject(complexRow.toLowerCase()) != null) {
                Long[] idsSqlArray = (Long[]) ((Array) rs.getObject(complexRow.toLowerCase())).getArray();

                List<?> relatedObjects = Arrays.stream(idsSqlArray).map(id -> entityManager.findById(relatedClass, id)).toList();

                fieldInfo.setter().invoke(instance, relatedObjects);
            }
        }
    }

    private Class<?> extractParameterizedCollectionClass(FieldInfo fieldInfo) {
        ParameterizedType collectionParameterizedType = (ParameterizedType) fieldInfo.field().getGenericType();
        Class<?> collectionParameterizedClass = (Class<?>) collectionParameterizedType.getActualTypeArguments()[0];
        return collectionParameterizedClass;
    }
}
