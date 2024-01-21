package ru.nsu.ccfit.orm.core.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.configuration.EntityManagementModule;


/**
 * Тестовый класс, работу приложения см. ru.nsu.ccfit.orm.core.meta.manager.DefaultEntityManagerTest
 */
public class Test {
    private static final Logger test = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new EntityManagementModule());
    }
}
