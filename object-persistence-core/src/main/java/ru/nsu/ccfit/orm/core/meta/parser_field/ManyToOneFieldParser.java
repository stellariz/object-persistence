package ru.nsu.ccfit.orm.core.meta.parser_field;

import java.lang.reflect.Field;
import ru.nsu.ccfit.orm.model.annotations.ManyToOne;

public class ManyToOneFieldParser implements FieldParser{
    @Override
    public boolean isApplicable(Field field) {
        return field.getAnnotation(ManyToOne.class) != null;
    }

    @Override
    public void fillData(ParseContext parseContext) {
        parseContext.getManyToOneRelationshipMap().put(parseContext.getField().getName(), parseContext.getFieldInfo());
    }
}
