package ru.nsu.ccfit.orm.core.meta;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.ccfit.orm.core.meta.manager.DefaultEntityMetaDataManager;
import ru.nsu.ccfit.orm.core.meta.manager.EntityMetaDataManager;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.nsu.ccfit.orm.core.sql.query.QueryTestUtils.createFieldInfo;


class DefaultEntityMetaDataManagerTest {

    private static final String TABLE_NAME = "table_name";
    private final EntityMetaDataManager uut = new DefaultEntityMetaDataManager();

    @ParameterizedTest
    @MethodSource("testClassProvider")
    void saveMetaData(Class<?> clazz, TableMetaData expectedSaveResult) {

        if (expectedSaveResult == null) {
            assertThrows(IllegalArgumentException.class, () -> uut.saveMetaData(clazz));
        } else {
            TableMetaData actualTableMetaData = uut.saveMetaData(clazz);

            assertTrue(expectedSaveResult.toString().equals(actualTableMetaData.toString()));
        }

    }

    private static Stream<Arguments> testClassProvider() throws NoSuchFieldException, NoSuchMethodException {
        return Stream.of(
                Arguments.of(TestClassEntityWithNoIdField.class, null),
                Arguments.of(TestClassEntityWithTwoIdFields.class, null),
                Arguments.of(TestClassNoEntity.class, null),
                Arguments.of(TestClassEntityWithOneIdField.class, getExpectedTableMetaDataForSave())
        );
    }

    private static TableMetaData getExpectedTableMetaDataForSave() throws NoSuchFieldException, NoSuchMethodException {
        var idField = createFieldInfo(TestClassEntityWithOneIdField.class, "var2", int.class);
        var nonIdField = createFieldInfo(TestClassEntityWithOneIdField.class, "var1", String.class);
        return new TableMetaData(
                new AtomicLong(0),
                TABLE_NAME,
                new IdRowData("var2", idField),
                new LinkedHashMap<>() {{
                    put("var1", nonIdField);
                    put("var2", idField);
                }},
                new LinkedHashMap<>() {{
                    put("var1", nonIdField);
                }},
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap()
        );
    }


    @Entity(name = TABLE_NAME)
    private static class TestClassEntityWithNoIdField {
        public String var1;
        private int var2;
    }

    @Entity(name = TABLE_NAME)
    private static class TestClassEntityWithTwoIdFields {
        @Id
        public String var1;
        @Id
        private int var2;
    }

    private static class TestClassNoEntity {
        public String var1;
        private int var2;
    }

    @Entity(name = TABLE_NAME)
    private static class TestClassEntityWithOneIdField {
        public String var1;
        @Id
        private int var2;

        public String getVar1() {
            return var1;
        }

        public int getVar2() {
            return var2;
        }

        public void setVar1(String var1) {
            this.var1 = var1;
        }

        public void setVar2(int var2) {
            this.var2 = var2;
        }
    }
}