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

/**
 * Тестовый класс
 * TODO: 04.12.2023 (r.popov) удалить после успешного тестирования библиотеки
 */
public class Test {
    private static final Logger test = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new EntityManagementModule());
        EntityManager<TestClass> manager = injector.getInstance(EntityManager.class);

        var obj = new TestClass(1, "TEST", 1);
        manager.createTableForClass(TestClass.class);
        manager.create(obj);
        System.out.println(manager.findById(TestClass.class, 1));
        manager.update(new TestClass(1, "TEST_3", 1));
        System.out.println(manager.findById(TestClass.class, 1));
        manager.delete(obj);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity(name = "test_class")
    public static class TestClass {
        @Id
        private int var1;
        private String var2;
        private double var3;
    }
}
