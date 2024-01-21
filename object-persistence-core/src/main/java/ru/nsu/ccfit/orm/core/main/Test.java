package ru.nsu.ccfit.orm.core.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.configuration.EntityManagementModule;
import ru.nsu.ccfit.orm.core.meta.manager.EntityManager;
import ru.nsu.ccfit.orm.model.annotations.*;
import ru.nsu.ccfit.orm.model.common.OrderType;

import java.util.Collections;
import java.util.List;

import static ru.nsu.ccfit.orm.core.repository.dsl.selector.EntitySelector.*;


/**
 * Тестовый класс
 * TODO: 04.12.2023 (r.popov) удалить после успешного тестирования библиотеки
 */
public class Test {
    private static final Logger test = LoggerFactory.getLogger(Test.class);
    
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new EntityManagementModule());
        EntityManager manager = injector.getInstance(EntityManager.class);
        
        manager.createTableForClass(InnerTestClass.class);
        manager.createTableForClass(TestClass.class);
        
        createComplexObject(manager);
    }
    
    private static void createComplexObject(EntityManager entityManager) {
        
        var testClassOneToMany = new TestClass(1L, "ManyToOne", 1.0, null, Collections.emptyList());
        var firstInnerTestClass = new InnerTestClass(1L, "OneToManyTest1", 1337, testClassOneToMany);
        var secondInnerTestClass = new InnerTestClass(2L, "OneToManyTest2", 322, testClassOneToMany);
        var thirdInnerTestClass = new InnerTestClass(3L, "OneToOneTest", 111, testClassOneToMany);
        var testClass = new TestClass(4L, "TEST", 1, thirdInnerTestClass, List.of(firstInnerTestClass, secondInnerTestClass));
        
//        entityManager.create(testClassOneToMany);
//        entityManager.create(firstInnerTestClass);
//        entityManager.create(secondInnerTestClass);
//        entityManager.create(thirdInnerTestClass);
//        entityManager.create(testClass);
        
        var res = entityManager.customSearch(TestClass.class)
            .where(
                or(
                    or(
                        equal("var2", "ManyToOne"),
                        equal("var3", 99.0)
                    ),
                    or(
                        equal("var2", "TEST"),
                        equal("var3", 99.0)
                    )
                )
            )
            .limit(10)
            .orderBy("var3", OrderType.DESCENDING)
            .toList();
        
        res.forEach(System.out::println);
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
