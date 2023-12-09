package ru.nsu.ccfit.orm.core.sql.query;

import ru.nsu.ccfit.orm.model.meta.TableMetaData;


/**
 * Утилитный класс для построения SQL запросов в БД
 */
public interface QueryBuilder {

    String buildSqlCreateTableQuery(TableMetaData tableMetaData);
}
