package ru.nsu.ccfit.orm.core.meta;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import static ru.nsu.ccfit.orm.core.utils.FieldUtilsManager.getFieldValue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public class ValuesCollector {
    private final EntityMetaDataManager entityMetaDataManager;

    public List<Object> collectAllValues(TableMetaData tableMetaData, Object instance) {
        // !!! dependency on order !!!
        List<Object> values = new ArrayList<>();

        Optional.ofNullable(getFieldValue(tableMetaData.idRowData().fieldInfo(), instance))
                .ifPresentOrElse(value -> {
                            values.add(value);
                            // if the key is more than counter - increase the counter =)
                            tableMetaData.counter().set((Long) value);
                        },
                        () -> values.add(tableMetaData.counter().incrementAndGet()));

        values.addAll(tableMetaData.simpleRowsData().values().stream()
                .map(value -> getFieldValue(value, instance))
                .toList());

        values.addAll(tableMetaData.oneToOneRowsData().values().stream()
                .map(value -> {
                    // try - catch by reflection = <3
                    value.field().setAccessible(true);
                    try {
                        if (value.field().get(instance) != null) {
                            return entityMetaDataManager.unsafeGetMetaData(value.getter().getReturnType()).counter()
                                    .longValue();
                        }
                    } catch (IllegalAccessException e) {
                    }
                    return null;
                })
                .toList());

        return values;
    }

    public Map<TableMetaData, Object> collectOneToOneValues(TableMetaData tableMetaData, Object instance) {
        Map<TableMetaData, Object> map = new HashMap<>();

        tableMetaData.oneToOneRowsData().values().forEach(
                fieldInfo -> map.put(
                        entityMetaDataManager.unsafeGetMetaData(fieldInfo.getter().getReturnType()),
                        getFieldValue(fieldInfo, instance))
        );

        return map;
    }
}
