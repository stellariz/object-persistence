package ru.nsu.ccfit.orm.core.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.configuration.EntityManagementModule;
import ru.nsu.ccfit.orm.core.meta.EntityManager;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;
import ru.nsu.ccfit.orm.model.annotations.OneToOne;

/**
 * Тестовый класс
 * TODO: 04.12.2023 (r.popov) удалить после успешного тестирования библиотеки
 */
public class Test {
    private static final Logger test = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new EntityManagementModule());
        EntityManager<TestClass> manager = injector.getInstance(EntityManager.class);
        var objTestRelationShip = new TestOneToOneClass(2L, "OneToOneTest", 2);
        manager.createTableForClass(TestOneToOneClass.class);

        var obj = new TestClass(4L, "TEST", 1, objTestRelationShip);
        manager.createTableForClass(TestClass.class);

        var createdObj = manager.create(obj);
        // check the counter element and related entity
        System.out.println(manager.findById(TestClass.class, 4));
        System.out.println(manager.findById(TestOneToOneClass.class, 2));

        var secondObjWithNullRelationShip = new TestClass(null, "TEST2", 0.0, null);
        var secondCreatedObj = manager.create(secondObjWithNullRelationShip);

        // check the counter next element
        System.out.println(manager.findById(TestClass.class, 5));


//        manager.delete(createdObj);
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
        private TestOneToOneClass var4;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity(name = "test_class_one_to_one")
    public static class TestOneToOneClass {
        @Id
        private Long var1;
        private String var2;
        private double var3;
    }
}
