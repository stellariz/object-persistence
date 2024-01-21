package ru.nsu.ccfit.orm.core.meta;


import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import ru.nsu.ccfit.orm.core.meta.manager.BasicValuesCollector;
import ru.nsu.ccfit.orm.core.meta.manager.DefaultEntityMetaDataManager;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.annotations.OneToOne;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasicValuesCollectorTest {

    AutoCloseable openMocks;

    @Spy
    private DefaultEntityMetaDataManager defaultEntityMetaDataManager;

    @InjectMocks
    private BasicValuesCollector uut;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCollectValueWithNonNullId() {
        TableMetaData metaData = defaultEntityMetaDataManager.saveMetaData(TestOneToOneClass.class);
        TestOneToOneClass testClass = new TestOneToOneClass(0L, "", 0.0);

        var actualRes = uut.collectColumnAndValuesPairs(metaData, testClass);

        assertEquals(Map.of("var1", 0L, "var2", "", "var3", 0.0), actualRes);
    }

    @Test
    void testCollectValueWithNullId() {
        TableMetaData metaData = defaultEntityMetaDataManager.saveMetaData(TestOneToOneClass.class);
        TestOneToOneClass testClass = new TestOneToOneClass(null, "", 0.0);

        var actualRes = uut.collectColumnAndValuesPairs(metaData, testClass);

        assertEquals(Map.of("var1", 1L, "var2", "", "var3", 0.0), actualRes);
    }

    @Test
    void testCollectValueWithOneToOne() {
        defaultEntityMetaDataManager.saveMetaData(TestOneToOneClass.class);
        TableMetaData metaDataForMainEntity = defaultEntityMetaDataManager.saveMetaData(TestClass.class);
        TestOneToOneClass testClass = new TestOneToOneClass(100L, "", 0.0);
        TestClass mainTestClass = new TestClass(null, "1", 1.0, testClass);

        var actualRes = uut.collectColumnAndValuesPairs(metaDataForMainEntity, mainTestClass);

        assertEquals(Map.of("var1", 1L, "var2", "1", "var3", 1.0, "var4", 100L), actualRes);
    }


    @Data
    @AllArgsConstructor
    @Entity
    public static class TestClass {
        @Id
        private Long var1;
        private String var2;
        private double var3;
        @OneToOne
        private TestOneToOneClass var4;
    }

    @Data
    @AllArgsConstructor
    @Entity
    public static class TestOneToOneClass {
        @Id
        private Long var1;
        private String var2;
        private double var3;
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}