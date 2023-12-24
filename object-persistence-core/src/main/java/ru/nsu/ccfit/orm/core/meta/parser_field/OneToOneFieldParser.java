package ru.nsu.ccfit.orm.core.meta.parser_field;

import java.lang.reflect.Field;
import ru.nsu.ccfit.orm.model.annotations.OneToOne;

public class OneToOneFieldParser implements FieldParser {
    @Override
    public boolean isApplicable(Field field) {
        return field.getAnnotation(OneToOne.class) != null;
    }

    @Override
    public void fillData(ParseContext parseContext) {
        parseContext.getOneToOneRelationshipMap().put(parseContext.getField().getName(), parseContext.getFieldInfo());
    }
}
