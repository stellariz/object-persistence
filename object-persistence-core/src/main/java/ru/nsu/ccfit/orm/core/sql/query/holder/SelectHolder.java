package ru.nsu.ccfit.orm.core.sql.query.holder;

import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.sql.query.builder.SelectBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.table.Column;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearExtraSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.enumerateBuildableWithSeparator;
import static ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol.COMMA;

public class SelectHolder implements SQLBuilder, ValuesProvider {
    
    private boolean selectAll = false;
    private List<Column> columns = new LinkedList<>();
    private List<SelectBuilder> selectBuilders = new LinkedList<>();
    
    public SelectHolder selectAll() {
        selectAll = true;
        return this;
    }
    
    public SelectHolder addSelect(Object... selectObjects) {
        Arrays.asList(selectObjects).forEach(this::addByObjectType);
        return this;
    }
    
    /**
     * Add components for SELECT query
     * 1. Add Column by markdown name
     * 2. Add Column straightforward
     * 3. Add internal SELECT
     */
    private void addByObjectType(Object object) {
        switch (object) {
            case String columnMarkdown -> columns.add(Column.fromMarkdown(columnMarkdown));
            case Column column -> columns.add(column);
            case SelectBuilder selectBuilder -> selectBuilders.add(selectBuilder);
            default -> throw new IllegalStateException("Unexpected value: " + object);
        }
    }
    
    @Override
    public String buildSQL() {
        return clearExtraSpaces("SELECT %s".formatted(
            selectAll ? Symbol.ALL : enumerateBuildableWithSeparator(columns, COMMA.getSymbol())
        ));
    }
    
    @Override
    public boolean shouldCreateSQL() {
        return CollectionUtils.isNotEmpty(columns) || CollectionUtils.isNotEmpty(selectBuilders) || selectAll;
    }
    
    @Override
    public List<?> provideValues() {
        return List.of();
    }
}
