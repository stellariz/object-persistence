package ru.nsu.ccfit.orm.core.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Тестовый класс
 * TODO: 04.12.2023 (r.popov) удалить после успешного тестирования библиотеки
 */
public class Test {
    private static final Logger test = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        test.error("hello");
    }
}
