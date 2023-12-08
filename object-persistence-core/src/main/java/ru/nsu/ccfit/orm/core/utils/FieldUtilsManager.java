package ru.nsu.ccfit.orm.core.utils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.reflect.FieldUtils;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

public class FieldUtilsManager {

    private FieldUtilsManager() {
    }

    public static boolean isOnlyOneIdField(Class<?> clazz) {
        return FieldUtils.getFieldsListWithAnnotation(clazz, Id.class).size() == 1;
    }

    public static boolean doesExistEntityAnnotation(Class<?> clazz) {
        return clazz.isAnnotationPresent(Entity.class);
    }

    public static Object getFieldValue(FieldInfo fieldInfo, Object instance) {
        try {
            return fieldInfo.getter().invoke(instance);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
