package ru.nsu.ccfit.orm.core.meta.parser_field;

import java.lang.reflect.Field;

public class AllFieldParser implements FieldParser {
    @Override
    public boolean isApplicable(Field field) {
        return true;
    }

    @Override
    public void fillData(ParseContext parseContext) {
        parseContext.getAllRowsMap().put(parseContext.getField().getName(), parseContext.getFieldInfo());
    }
}
