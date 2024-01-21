package ru.nsu.ccfit.orm.core.meta.parser_field;

import java.lang.reflect.Field;
import ru.nsu.ccfit.orm.model.annotations.OneToMany;

public class OneToManyFieldParser implements FieldParser {
    @Override
    public boolean isApplicable(Field field) {
        return field.getAnnotation(OneToMany.class) != null;
    }

    @Override
    public void fillData(ParseContext parseContext) {
        parseContext.getOneToManyRelationshipMap().put(parseContext.getField().getName(), parseContext.getFieldInfo());
    }
}
