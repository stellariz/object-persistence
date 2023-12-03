package ru.nsu.ccfit.orm.core.sql.query;

import java.util.List;

/**
 * Утилитный класс для построения SQL запросов в БД
 */
public interface QueryBuilder {
    String buildSqlQuery(List<String> params);
}
