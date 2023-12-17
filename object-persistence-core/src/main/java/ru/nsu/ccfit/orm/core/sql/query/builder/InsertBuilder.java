package ru.nsu.ccfit.orm.core.sql.query.builder;

import ru.nsu.ccfit.orm.core.sql.query.common.PreparedStatementBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.holder.HolderType;
import ru.nsu.ccfit.orm.core.sql.query.holder.InsertHolder;
import ru.nsu.ccfit.orm.core.sql.query.holder.ReturningHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;

public class InsertBuilder implements SQLBuilder, PreparedStatementBuilder, ValuesProvider {

    private Map<HolderType, SQLBuilder> holders = new LinkedHashMap<>() {{
        put(HolderType.INSERT_HOLDER, new InsertHolder());
        put(HolderType.RETURNING_HOLDER, new ReturningHolder());
    }};


    private InsertBuilder() {
    }

    public static InsertBuilder createInsertForTable() {
        return new InsertBuilder();
    }

    public InsertBuilder table(String tableEncoding) {
        if (holders.get(HolderType.INSERT_HOLDER) instanceof InsertHolder ih) {
            ih.table(tableEncoding);
        }
        return this;
    }

    public InsertBuilder insertSet(Map<String, ?> insertSet) {
        if (holders.get(HolderType.INSERT_HOLDER) instanceof InsertHolder ih) {
            ih.insertSet(insertSet);
        }
        return this;
    }

    public InsertBuilder returning(String... columnsEncodings) {
        if (holders.get(HolderType.RETURNING_HOLDER) instanceof ReturningHolder ih) {
            ih.addColumns(columnsEncodings);
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
        if (holders.get(HolderType.INSERT_HOLDER) instanceof InsertHolder ih) {
            return ih.shouldCreateSQL();
        }
        return false;
    }

    @Override
    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
//        var preparedStatement = returning != null ?
//                connection.prepareStatement(buildSQL(), RETURN_GENERATED_KEYS) : connection.prepareStatement(buildSQL());
//        fillPreparedStatement(preparedStatement, provideValues());
        return null;
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
