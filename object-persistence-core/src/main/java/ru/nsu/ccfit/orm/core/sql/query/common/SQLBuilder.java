package ru.nsu.ccfit.orm.core.sql.query.common;

public interface SQLBuilder {

    String buildSQL();
    default boolean shouldCreateSQL() {
        return false;
    }

}
