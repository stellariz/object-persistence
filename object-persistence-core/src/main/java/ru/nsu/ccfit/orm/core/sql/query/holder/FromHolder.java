package ru.nsu.ccfit.orm.core.sql.query.holder;

import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Table;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.enumerateBuildableWithSeparator;
import static ru.nsu.ccfit.orm.core.sql.query.common.element.Table.fromMarkdowns;

public class FromHolder implements SQLBuilder {
    private List<Table> tables = new ArrayList<>();

    public FromHolder addTables(Table... tables) {
        this.tables.addAll(List.of(tables));
        return this;
    }

    public FromHolder addTables(String... tableEncodings) {
        this.tables.addAll(fromMarkdowns(tableEncodings));
        return this;
    }

    @Override
    public String buildSQL() {
        return clearDublicatedSpaces("%s %s".formatted(
                KeyWord.FROM, enumerateBuildableWithSeparator(tables, Symbol.COMMA.getSymbol())
        ));
    }

    @Override
    public boolean shouldCreateSQL() {
        return CollectionUtils.isNotEmpty(tables);
    }
}
