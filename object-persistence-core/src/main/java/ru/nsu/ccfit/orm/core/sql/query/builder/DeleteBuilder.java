package ru.nsu.ccfit.orm.core.sql.query.builder;

import ru.nsu.ccfit.orm.core.sql.query.common.PreparedStatementBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionSignature;
import ru.nsu.ccfit.orm.core.sql.query.holder.ConditionsHolder;
import ru.nsu.ccfit.orm.core.sql.query.holder.DeleteHolder;
import ru.nsu.ccfit.orm.core.sql.query.holder.FromHolder;
import ru.nsu.ccfit.orm.core.sql.query.holder.HolderType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.fillPreparedStatement;

public class DeleteBuilder implements SQLBuilder, PreparedStatementBuilder, ValuesProvider {

    private Map<HolderType, SQLBuilder> holders = new LinkedHashMap<>() {{
        put(HolderType.DELETE_HOLDER, new DeleteHolder());
        put(HolderType.FROM_HOLDER, new FromHolder());
        put(HolderType.CONDITIONS_HOLDER, new ConditionsHolder());
    }};

    private DeleteBuilder() {
    }

    public static DeleteBuilder createDelete() {
        return new DeleteBuilder();
    }

    public DeleteBuilder addTables(String... tablesNames) {
        if (holders.get(HolderType.DELETE_HOLDER) instanceof DeleteHolder dh) {
            dh.addTables(tablesNames);
        }
        return this;
    }

    public DeleteBuilder from(String... fromTables) {
        if (holders.get(HolderType.FROM_HOLDER) instanceof FromHolder fh) {
            fh.addTables(fromTables);
        }
        return this;
    }

    public DeleteBuilder where(ConditionSignature... conditionSignatures) {
        if (holders.get(HolderType.CONDITIONS_HOLDER) instanceof ConditionsHolder ch) {
            Arrays.stream(conditionSignatures).forEach(ch::addCondition);
        }
        return this;
    }

    @Override
    public String buildSQL() {
        StringBuilder result = holders.values().stream()
                .filter(SQLBuilder::shouldCreateSQL)
                .map(SQLBuilder::buildSQL)
                .reduce(new StringBuilder(), (e1, e2) -> e1.append(Symbol.SPACE).append(e2), StringBuilder::append);

        return clearDublicatedSpaces(result.substring(1));
    }

    @Override
    public boolean shouldCreateSQL() {
        if (holders.get(HolderType.DELETE_HOLDER) instanceof DeleteHolder dh) {
            return dh.shouldCreateSQL();
        }
        return false;
    }

    @Override
    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        var preparedStatement = connection.prepareStatement(buildSQL());
        fillPreparedStatement(preparedStatement, provideValues());
        return preparedStatement;
    }

    @Override
    public List<?> provideValues() {
        return holders.values().stream()
                .filter(h -> h instanceof ValuesProvider)
                .map(h -> (ValuesProvider) h)
                .flatMap(h -> h.provideValues().stream())
                .toList();
    }
}
