package ru.nsu.ccfit.orm.core.sql.query.holder;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Table;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.enumerateBuildableWithSeparator;
import static ru.nsu.ccfit.orm.core.sql.query.common.element.Table.fromMarkdowns;

public class DeleteHolder implements SQLBuilder {

    private List<Table> tables = new ArrayList<>();

    public DeleteHolder addTables(String... tableEncodings) {
        this.tables.addAll(fromMarkdowns(tableEncodings));
        return this;
    }

    @Override
    public String buildSQL() {
        return clearDublicatedSpaces("%s %s".formatted(
                KeyWord.DELETE, enumerateBuildableWithSeparator(tables, Symbol.COMMA.getSymbol())
        ));
    }

    @Override
    public boolean shouldCreateSQL() {
        return true;
    }
}
