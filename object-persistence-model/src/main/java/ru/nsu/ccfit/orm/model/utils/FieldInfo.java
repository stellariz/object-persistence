package ru.nsu.ccfit.orm.model.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public record FieldInfo(Field field, Method getter, Method setter) {
}
