package ru.nsu.ccfit.orm.core.meta;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import ru.nsu.ccfit.orm.core.meta.parser_field.AllFieldParser;
import ru.nsu.ccfit.orm.core.meta.parser_field.FieldParser;
import ru.nsu.ccfit.orm.core.meta.parser_field.OneToOneFieldParser;
import ru.nsu.ccfit.orm.core.meta.parser_field.ParseContext;
import ru.nsu.ccfit.orm.core.meta.parser_field.SimpleFieldParser;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

public class TableMetaDataBuilder {

    private final List<FieldParser> fieldParserList;

    {
        fieldParserList = new ArrayList<>();
        fieldParserList.add(new AllFieldParser());
        fieldParserList.add(new OneToOneFieldParser());
        fieldParserList.add(new SimpleFieldParser());
    }

    public TableMetaData buildTableMetaDataByClass(Class<?> clazz) {
        String tableName = clazz.getAnnotation(Entity.class).name();
        IdRowData idRowData = null;
        ParseContext parseContext = new ParseContext();
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Field field = clazz.getDeclaredField(propertyDescriptor.getName());
                    FieldInfo fieldInfo = new FieldInfo(
                            field, propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod()
                    );
                    if (field.getAnnotation(Id.class) != null) {
                        idRowData = new IdRowData(field.getName(), fieldInfo);
                    }
                    parseContext.setField(field);
                    parseContext.setFieldInfo(fieldInfo);
                    for (var fieldParser : fieldParserList) {
                        if (fieldParser.isApplicable(field)) {
                            fieldParser.fillData(parseContext);
                        }
                    }
                }
            }
        } catch (NoSuchFieldException | IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
        return new TableMetaData(new AtomicLong(0), tableName, idRowData, parseContext.getAllRowsMap(),
                parseContext.getSimpleFieldInfoMap(), parseContext.getOneToOneRelationshipMap());
    }
}
