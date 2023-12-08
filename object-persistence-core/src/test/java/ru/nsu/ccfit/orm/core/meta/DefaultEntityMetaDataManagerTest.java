package ru.nsu.ccfit.orm.core.meta;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DefaultEntityMetaDataManagerTest {

    private static final String TABLE_NAME = "table_name";
    private EntityMetaDataManager uut = new DefaultEntityMetaDataManager();

    @ParameterizedTest
    @MethodSource("testClassProvider")
    void saveMetaData(Class<?> clazz, TableMetaData expectedSaveResult) {

        if (expectedSaveResult == null) {
            assertThrows(IllegalArgumentException.class, () -> uut.saveMetaData(clazz));
        } else {
            TableMetaData actualTableMetaData = uut.saveMetaData(clazz);

            assertEquals(expectedSaveResult, actualTableMetaData);
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
        return new TableMetaData(TABLE_NAME,
                new IdRowData(
                        "var2", new FieldInfo(TestClassEntityWithOneIdField.class.getDeclaredField("var2"),
                        TestClassEntityWithOneIdField.class.getDeclaredMethod("getVar2"),
                        TestClassEntityWithOneIdField.class.getDeclaredMethod("setVar2", int.class))
                ),
                Map.of("var1", new FieldInfo(TestClassEntityWithOneIdField.class.getDeclaredField("var1"),
                                TestClassEntityWithOneIdField.class.getDeclaredMethod("getVar1"),
                                TestClassEntityWithOneIdField.class.getDeclaredMethod("setVar1", String.class)),
                        "var2", new FieldInfo(TestClassEntityWithOneIdField.class.getDeclaredField("var2"),
                                TestClassEntityWithOneIdField.class.getDeclaredMethod("getVar2"),
                                TestClassEntityWithOneIdField.class.getDeclaredMethod("setVar2", int.class))
                ));
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