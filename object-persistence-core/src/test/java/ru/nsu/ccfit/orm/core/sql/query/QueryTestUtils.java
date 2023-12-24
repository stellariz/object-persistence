package ru.nsu.ccfit.orm.core.sql.query;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashMap;

public class QueryTestUtils {

    @Data
    @Entity(name = "TEST_TABLE")
    public static class RichClass {
        @Id
        public int idField;
        public String stringField;
        private int intField;
        private double doubleField;
        private float floatField;
        private BigDecimal bigDecimalField;
        private BigInteger bigIntegerField;
        private Date dateField;
        private boolean booleanField;
    }

    public static TableMetaData buildRichClassMetaData() throws NoSuchFieldException, NoSuchMethodException {
        var idField = createFieldInfo(RichClass.class, "idField", int.class);
        var stringField = createFieldInfo(RichClass.class, "stringField", String.class);
        var intField = createFieldInfo(RichClass.class, "intField", int.class);
        var doubleField = createFieldInfo(RichClass.class, "doubleField", double.class);
        var floatField = createFieldInfo(RichClass.class, "floatField", float.class);
        var bigDecimalField = createFieldInfo(RichClass.class, "bigDecimalField", BigDecimal.class);
        var bigIntegerField = createFieldInfo(RichClass.class, "bigIntegerField", BigInteger.class);
        var booleanField = createFieldInfo(RichClass.class, "booleanField", boolean.class);
        var dateField = createFieldInfo(RichClass.class, "dateField", Date.class);
        return new TableMetaData(
                new AtomicLong(0),
                "TEST_TABLE",
                new IdRowData("idField", idField),
                new LinkedHashMap<>() {{
                    put("idField", idField);
                    put("stringField", stringField);
                    put("intField", intField);
                    put("doubleField", doubleField);
                    put("floatField", floatField);
                    put("bigDecimalField", bigDecimalField);
                    put("bigIntegerField", bigIntegerField);
                    put("booleanField", booleanField);
                    put("dateField", dateField);
                }},
                new LinkedHashMap<>() {{
                    put("stringField", stringField);
                    put("intField", intField);
                    put("doubleField", doubleField);
                    put("floatField", floatField);
                    put("bigDecimalField", bigDecimalField);
                    put("bigIntegerField", bigIntegerField);
                    put("booleanField", booleanField);
                    put("dateField", dateField);
                }},
                Collections.emptyMap()
        );
    }

    public static FieldInfo createFieldInfo(Class<?> parentClazz, String name, Class<?> childClazz) throws NoSuchFieldException, NoSuchMethodException {
        var getterPrefix =
                "boolean".equals(childClazz.getTypeName()) || "Boolean".equals(childClazz.getTypeName()) ? "is" : "get";
        return new FieldInfo(
                parentClazz.getDeclaredField(name),
                parentClazz.getDeclaredMethod(getterPrefix + StringUtils.capitalize(name)),
                parentClazz.getDeclaredMethod("set" + StringUtils.capitalize(name), childClazz)
        );
    }

}
