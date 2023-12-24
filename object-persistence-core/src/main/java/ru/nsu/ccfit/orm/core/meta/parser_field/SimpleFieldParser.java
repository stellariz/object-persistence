package ru.nsu.ccfit.orm.core.meta.parser_field;

import java.lang.reflect.Field;

public class SimpleFieldParser implements FieldParser {

    @Override
    public boolean isApplicable(Field field) {
        return field.getDeclaredAnnotations().length == 0;
    }

    @Override
    public void fillData(ParseContext parseContext) {
        parseContext.getSimpleFieldInfoMap().put(parseContext.getField().getName(), parseContext.getFieldInfo());
    }
}
