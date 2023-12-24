package ru.nsu.ccfit.orm.core.meta.parser_field;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

@Data
@NoArgsConstructor
public class ParseContext {
    private Field field;
    private FieldInfo fieldInfo;
    private final Map<String, FieldInfo> simpleFieldInfoMap = new LinkedHashMap<>();
    private final Map<String, FieldInfo> oneToOneRelationshipMap = new LinkedHashMap<>();
    private final Map<String, FieldInfo> allRowsMap = new LinkedHashMap<>();
}
