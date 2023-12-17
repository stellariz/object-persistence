package ru.nsu.ccfit.orm.core.sql.query.common.element.order;

import ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Column;

public record Order(Column column, KeyWord type) implements SQLBuilder {

    Order(String columnEncoding, KeyWord type) {
        this(Column.fromMarkdown(columnEncoding), type);
    }

    @Override
    public String buildSQL() {
        return BuilderUtils.clearDublicatedSpaces("%s %s".formatted(column.sqlRepresentation(), type.getKeyword()));
    }
}
