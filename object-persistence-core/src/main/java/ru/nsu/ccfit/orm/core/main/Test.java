package ru.nsu.ccfit.orm.core.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.meta.DefaultEntityManager;
import ru.nsu.ccfit.orm.core.meta.DefaultEntityMetaDataManager;
import ru.nsu.ccfit.orm.model.annotations.Entity;
import ru.nsu.ccfit.orm.model.annotations.Id;

/**
 * Тестовый класс
 * TODO: 04.12.2023 (r.popov) удалить после успешного тестирования библиотеки
 */
public class Test {
    private static final Logger test = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        test.error("hello");
        var entityMetaDataManager = new DefaultEntityMetaDataManager<TestClass>();
        var entityManager = new DefaultEntityManager<>(entityMetaDataManager);

       entityMetaDataManager.saveMetaData(TestClass.class);

        var testObject = new TestClass();
        testObject.setVar2("hello");
        entityManager.create(testObject);
    }

    @Entity(name = "test_class")
    public static class TestClass {
        @Id
        private int var1;

        private String var2;

        public int getVar1() {
            return var1;
        }

        public void setVar1(int var1) {
            this.var1 = var1;
        }

        public String getVar2() {
            return var2;
        }

        public void setVar2(String var2) {
            this.var2 = var2;
        }
    }
}
