package ru.nsu.ccfit.orm.core.meta.parser_field;

import java.lang.reflect.Field;

public interface FieldParser {
    boolean isApplicable(Field field);

    void fillData(ParseContext parseContext);
}
