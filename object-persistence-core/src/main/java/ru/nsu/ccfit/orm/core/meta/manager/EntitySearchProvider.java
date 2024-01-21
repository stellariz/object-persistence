package ru.nsu.ccfit.orm.core.meta.manager;

import ru.nsu.ccfit.orm.core.sql.query.builder.SelectBuilder;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.List;

public interface EntitySearchProvider {

    <T> List<? extends T> searchByQuery(SelectBuilder selectBuilder, TableMetaData tableMetaData, Class<T> objectClass);

}
