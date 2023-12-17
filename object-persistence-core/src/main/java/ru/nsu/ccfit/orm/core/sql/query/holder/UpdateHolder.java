package ru.nsu.ccfit.orm.core.sql.query.holder;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.table.Table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearExtraSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.enumerateObjectsWithSeparator;
import static ru.nsu.ccfit.orm.core.sql.query.common.element.table.Table.fromMarkdown;

public class UpdateHolder implements SQLBuilder, ValuesProvider {

    private Table table;
    private Map<String, ?> updateSet = new LinkedHashMap<>();

    public UpdateHolder table(String tableEncoding) {
        this.table = fromMarkdown(tableEncoding);
        return this;
    }

    public UpdateHolder updateSet(Map<String, ?> updateSet) {
        this.updateSet = updateSet;
        return this;
    }

    private List<String> getValuesChanges() {
        return updateSet.keySet().stream()
                .map("%s = ?"::formatted)
                .toList();
    }

    @Override
    public String buildSQL() {
        return clearExtraSpaces("UPDATE %s SET %s".formatted(
                table.buildSQL(), enumerateObjectsWithSeparator(getValuesChanges(), Symbol.COMMA.getSymbol())
        ));
    }

    @Override
    public boolean shouldCreateSQL() {
        return table != null && !updateSet.isEmpty();
    }

    @Override
    public List<?> provideValues() {
        return new ArrayList<>(updateSet.values());
    }
}
