package ru.nsu.ccfit.orm.core.meta;


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

//        List<Object> objects = uut.collectColumnAndValuesPairs(metaData, testClass);
//
//        var expectedValues = List.of(0L, "", 0.0);
//        assertEquals(expectedValues, objects);
    }

    @Test
    void testCollectValueWithNullId() {
        TableMetaData metaData = defaultEntityMetaDataManager.saveMetaData(TestOneToOneClass.class);
        TestOneToOneClass testClass = new TestOneToOneClass(null, "", 0.0);

//        List<Object> objects = uut.collectColumnAndValuesPairs(metaData, testClass);
//
//        var expectedValues = List.of(0L, "", 0.0);
//        assertEquals(expectedValues, objects);
    }

    @Test
    void testCollectValueWithOneToOne() {
        TableMetaData metaData = defaultEntityMetaDataManager.saveMetaData(TestOneToOneClass.class);
        TableMetaData metaDataForMainEntity = defaultEntityMetaDataManager.saveMetaData(TestClass.class);
        TestOneToOneClass testClass = new TestOneToOneClass(null, "", 0.0);
        TestClass mainTestClass = new TestClass(null, "1", 1.0, testClass);

//        List<Object> objects = uut.collectColumnAndValuesPairs(metaDataForMainEntity, mainTestClass);
//
//        var expectedValues = List.of(0L, "1", 1.0, 0L);
//        assertEquals(expectedValues, objects);
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