package ru.nsu.ccfit.orm.model.utils;

/**
 * Класс, содержащий информацию о поле, выступающее в качестве Primary Key
 */
public record IdRowData(String idFieldName, FieldInfo fieldInfo) {
}
