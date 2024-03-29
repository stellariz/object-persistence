package ru.nsu.ccfit.orm.core.sql.query.common.element.join;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.element.table.Column;
import ru.nsu.ccfit.orm.core.sql.query.common.element.table.Table;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearExtraSpaces;

public record Join(JoinType joinType,
                   Table table,
                   Column leftJoinColumn,
                   Column rightJoinColumn) implements SQLBuilder {

    Join(JoinType type, String tableOrTableMarkdown, String leftJoinOn, String rightJoinOn) {
        this(
                type,
                Table.fromMarkdown(tableOrTableMarkdown),
                Column.fromMarkdown(leftJoinOn),
                Column.fromMarkdown(rightJoinOn)
        );
    }

    @Override
    public String buildSQL() {
        return clearExtraSpaces(
                "%s %s ON %s = %s".formatted(
                        joinType,
                        table.buildSQL(),
                        leftJoinColumn.sqlRepresentation(),
                        rightJoinColumn.sqlRepresentation()
                ));
    }

}
