package ru.nsu.ccfit.orm.core.sql.query.common.element.order;

import ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.element.table.Column;
import ru.nsu.ccfit.orm.model.common.OrderType;

public record Order(Column column, OrderType type) implements SQLBuilder {

    Order(String columnEncoding, OrderType type) {
        this(Column.fromMarkdown(columnEncoding), type);
    }

    @Override
    public String buildSQL() {
        return BuilderUtils.clearExtraSpaces("%s %s".formatted(column.sqlRepresentation(), type.getType()));
    }
}
