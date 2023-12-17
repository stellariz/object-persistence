package ru.nsu.ccfit.orm.core.sql.query.holder;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.table.Table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.*;
import static ru.nsu.ccfit.orm.core.sql.query.common.element.table.Table.fromMarkdown;

public class InsertHolder implements SQLBuilder, ValuesProvider {

    private Table table;
    private Map<String, ?> valueToColumnMapping = new LinkedHashMap<>();

    public InsertHolder table(String tableEncoding) {
        this.table = fromMarkdown(tableEncoding);
        return this;
    }

    public InsertHolder insertSet(Map<String, ?> insertSet) {
        this.valueToColumnMapping = insertSet;
        return this;
    }

    @Override
    public String buildSQL() {
        var columnNames = new ArrayList<>(valueToColumnMapping.keySet());
        var columnNamesEnumerated = enumerateObjectsWithSeparator(columnNames, Symbol.COMMA.getSymbol());
        var variables = enumerateVariablesWithSeparator(columnNames.size(), Symbol.COMMA.getSymbol());
        return clearExtraSpaces("INSERT INTO %s (%s) VALUES (%s)".formatted(
                table.buildSQL(), columnNamesEnumerated, variables)
        );
    }

    @Override
    public boolean shouldCreateSQL() {
        return table != null && !valueToColumnMapping.isEmpty();
    }

    @Override
    public List<?> provideValues() {
        return new ArrayList<>(valueToColumnMapping.values());
    }
}
