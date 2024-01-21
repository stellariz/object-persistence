package ru.nsu.ccfit.orm.core.meta.manager;


import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.nsu.ccfit.orm.core.repository.dsl.selector.EntitySelector;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.annotations.ManyToOne;
import ru.nsu.ccfit.orm.model.annotations.OneToMany;
import ru.nsu.ccfit.orm.model.annotations.OneToOne;
import ru.nsu.ccfit.orm.model.common.OrderType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.nsu.ccfit.orm.core.repository.dsl.selector.EntitySelector.equal;

class DefaultEntityManagerTest extends TestBase {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );
    private PGSimpleDataSource dataSource;

    private EntityManager entityManager;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Override
    public DataSource pgDataSource() {
        dataSource = new PGSimpleDataSource();
        dataSource.setURL(postgres.getJdbcUrl());
        dataSource.setUser(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());
        return dataSource;
    }

    @BeforeEach
    void initManager() {
        entityManager = injector.getInstance(EntityManager.class);
        entityManager.createTableForClass(InnerTestClass.class);
        entityManager.createTableForClass(TestClass.class);
    }

    @Test
    void testSave_ok() {
        var testClassOneToMany = new TestClass(1L, "ManyToOne", 1.0, null, Collections.emptyList());
        var firstInnerTestClass = new InnerTestClass(1L, "OneToManyTest1", 1337, testClassOneToMany);
        var secondInnerTestClass = new InnerTestClass(2L, "OneToManyTest2", 322, testClassOneToMany);
        var thirdInnerTestClass = new InnerTestClass(3L, "OneToOneTest", 111, testClassOneToMany);
        var testClass = new TestClass(4L, "TEST", 1, thirdInnerTestClass, List.of(firstInnerTestClass, secondInnerTestClass));
        entityManager.create(testClass);

        var actual = entityManager.findById(TestClass.class, 4);
        var actualOneToOneChildClass = entityManager.findById(InnerTestClass.class, 3L);
        var actualManyToOnelChildClass = entityManager.findById(InnerTestClass.class, 1L);
        var actualManyToOneChildClass = entityManager.findById(InnerTestClass.class, 2L);

        assertEquals(actual, testClass);
        assertEquals(thirdInnerTestClass, actualOneToOneChildClass);
        assertEquals(firstInnerTestClass, actualManyToOnelChildClass);
        assertEquals(secondInnerTestClass, actualManyToOneChildClass);
    }

    @Test
    void testDelete_ok() {
        var testClassOneToMany = new TestClass(1L, "ManyToOne", 1.0, null, Collections.emptyList());
        var firstInnerTestClass = new InnerTestClass(1L, "OneToManyTest1", 1337, testClassOneToMany);
        var secondInnerTestClass = new InnerTestClass(2L, "OneToManyTest2", 322, testClassOneToMany);
        var thirdInnerTestClass = new InnerTestClass(3L, "OneToOneTest", 111, testClassOneToMany);
        var testClass = new TestClass(4L, "TEST", 1, thirdInnerTestClass, List.of(firstInnerTestClass, secondInnerTestClass));
        var createdObj = entityManager.create(testClass);

        entityManager.delete(createdObj);
        var actualParentClass = entityManager.findById(TestClass.class, 4);
        var actualOneToOneChildClass = entityManager.findById(InnerTestClass.class, 3L);
        var actualManyToOnelChildClass = entityManager.findById(InnerTestClass.class, 1L);
        var actualManyToOneChildClass = entityManager.findById(InnerTestClass.class, 2L);

        assertNull(actualParentClass);
        assertNull(actualOneToOneChildClass);
        assertNull(actualManyToOnelChildClass);
        assertNull(actualManyToOneChildClass);
    }

    @Test
    void testUpdate_ok() {
        var testClassOneToMany = new TestClass(1L, "ManyToOne", 1.0, null, Collections.emptyList());
        var firstInnerTestClass = new InnerTestClass(1L, "OneToManyTest1", 1337, testClassOneToMany);
        var secondInnerTestClass = new InnerTestClass(2L, "OneToManyTest2", 322, testClassOneToMany);
        var thirdInnerTestClass = new InnerTestClass(3L, "OneToOneTest", 111, testClassOneToMany);
        var testClass = new TestClass(4L, "TEST", 1, thirdInnerTestClass, List.of(firstInnerTestClass, secondInnerTestClass));
        entityManager.create(testClass);
        testClass.setVar3(190.0);
        entityManager.update(testClass);

        var actualUpdatedClassEntity = entityManager.findById(TestClass.class, 4);

        assertEquals(testClass, actualUpdatedClassEntity);
    }


    @Test
    void testDsl_ok() {
        var testClassOneToMany = new TestClass(1L, "ManyToOne", 1.0, null, Collections.emptyList());
        var firstInnerTestClass = new InnerTestClass(1L, "OneToManyTest1", 1337, testClassOneToMany);
        var secondInnerTestClass = new InnerTestClass(2L, "OneToManyTest2", 322, testClassOneToMany);
        var thirdInnerTestClass = new InnerTestClass(3L, "OneToOneTest", 111, testClassOneToMany);
        var testClass = new TestClass(4L, "TEST", 1, thirdInnerTestClass, List.of(firstInnerTestClass, secondInnerTestClass));
        entityManager.create(testClass);

        var actualRes = entityManager.customSearch(InnerTestClass.class)
                .where(
                        EntitySelector.or(
                                equal("var3", 1337.0),
                                equal("var3", 322.0)
                        )
                )
                .limit(10)
                .orderBy("var3", OrderType.ASCENDING)
                .toList();

        assertEquals(actualRes, List.of(secondInnerTestClass, firstInnerTestClass));
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity(name = "test_class")
    public static class TestClass {
        @Id
        private Long var1;
        private String var2;
        private double var3;
        @OneToOne
        private InnerTestClass var4;
        @OneToMany
        private List<InnerTestClass> var5;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity(name = "inner_test_class")
    public static class InnerTestClass {
        @Id
        private Long var1;
        private String var2;
        private double var3;
        @ManyToOne
        private TestClass var4;
    }
}