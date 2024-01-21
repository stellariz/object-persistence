package ru.nsu.ccfit.orm.core.meta.manager;

import com.google.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ru.nsu.ccfit.orm.core.meta.manager.collectors.IdDataFieldsCollector;
import ru.nsu.ccfit.orm.core.meta.manager.collectors.ManyToOneRelationshipFieldsCollector;
import ru.nsu.ccfit.orm.core.meta.manager.collectors.ObjectFieldsCollector;
import ru.nsu.ccfit.orm.core.meta.manager.collectors.OneToManyRelationshipFieldsCollector;
import ru.nsu.ccfit.orm.core.meta.manager.collectors.OneToOneRelationshipFieldsCollector;
import ru.nsu.ccfit.orm.core.meta.manager.collectors.SimpleRowsFieldsCollector;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

import static ru.nsu.ccfit.orm.core.utils.FieldUtilsManager.getFieldValue;

public class BasicValuesCollector implements ValuesCollector {
    private final EntityMetaDataManager entityMetaDataManager;
    private final List<ObjectFieldsCollector> objectFieldsCollectors;

    @Inject
    private BasicValuesCollector(EntityMetaDataManager entityMetaDataManager) {
        this.entityMetaDataManager = entityMetaDataManager;
        this.objectFieldsCollectors = createCollectors(entityMetaDataManager);
    }


    private List<ObjectFieldsCollector> createCollectors(EntityMetaDataManager entityMetaDataManager) {
        return new LinkedList<>(List.of(
                new IdDataFieldsCollector(),
                new SimpleRowsFieldsCollector(),
                new OneToOneRelationshipFieldsCollector(entityMetaDataManager),
                new OneToManyRelationshipFieldsCollector(entityMetaDataManager),
                new ManyToOneRelationshipFieldsCollector(entityMetaDataManager)
        ));
    }


    @Override
    public Map<String, Object> collectColumnAndValuesPairs(TableMetaData tableMetaData, Object instance) {
        // !!! dependency on order !!!
        Map<String, Object> columnsWithValues = new LinkedHashMap<>();

        objectFieldsCollectors.forEach(objectCollector -> objectCollector
                .collectFields(tableMetaData, instance, columnsWithValues));

        return columnsWithValues;
    }

    @Override
    public Map<TableMetaData, Object> collectOneToOneValues(TableMetaData tableMetaData, Object instance) {
        Map<TableMetaData, Object> map = new HashMap<>();

        tableMetaData.oneToOneRowsData().values().forEach(
                fieldInfo -> map.put(
                        entityMetaDataManager.unsafeGetMetaData(fieldInfo.getter().getReturnType()),
                        getFieldValue(fieldInfo, instance)
                ));

        return map;
    }

    @Override
    public Map<TableMetaData, List<Object>> collectOneToManyValues(TableMetaData tableMetaData, Object instance) {
        Map<TableMetaData, List<Object>> map = new HashMap<>();

        tableMetaData.oneToManyRowsData().values().forEach(
                fieldInfo -> {
                    var collectionParameterizedClass = extractParameterizedCollectionClass(fieldInfo);
                    map.put(
                            entityMetaDataManager.unsafeGetMetaData(collectionParameterizedClass),
                            (List<Object>) getFieldValue(fieldInfo, instance));
                }
        );

        return map;
    }

    @Override
    public Map<TableMetaData, Object> collectManyToOneValues(TableMetaData tableMetaData, Object instance) {
        Map<TableMetaData, Object> map = new HashMap<>();

        tableMetaData.manyToOneRowsData().values().forEach(
                fieldInfo -> map.put(
                        entityMetaDataManager.unsafeGetMetaData(fieldInfo.getter().getReturnType()),
                        getFieldValue(fieldInfo, instance)
                )
        );

        return map;
    }

    private Class<?> extractParameterizedCollectionClass(FieldInfo fieldInfo) {
        ParameterizedType collectionParameterizedType = (ParameterizedType) fieldInfo.field().getGenericType();
        Class<?> collectionParameterizedClass = (Class<?>) collectionParameterizedType.getActualTypeArguments()[0];
        return collectionParameterizedClass;
    }
}
