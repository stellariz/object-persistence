package ru.nsu.ccfit.orm.core.sql.query.holder;

import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Column;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.enumerateBuildableWithSeparator;

public class ReturningHolder implements SQLBuilder {
    private boolean returnAll;
    private List<Column> columns = new ArrayList<>();

    public ReturningHolder returnAll() {
        returnAll = true;
        return this;
    }

    public ReturningHolder addColumn(String columnEncoding) {
        columns.add(Column.fromMarkdown(columnEncoding));
        return this;
    }

    public ReturningHolder addColumns(String... columnEncoding) {
        columns.addAll(Column.fromMarkdowns(columnEncoding));
        return this;
    }

    @Override
    public String buildSQL() {
        return "RETURNING %s".formatted(
                returnAll ? "*" : enumerateBuildableWithSeparator(columns, Symbol.COMMA.getSymbol())
        );
    }

    @Override
    public boolean shouldCreateSQL() {
        return returnAll || CollectionUtils.isNotEmpty(columns);
    }

}