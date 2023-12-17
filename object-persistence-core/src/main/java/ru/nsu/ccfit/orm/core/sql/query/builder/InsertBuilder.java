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
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.*;

public class InsertBuilder implements SQLBuilder, PreparedStatementBuilder, ValuesProvider {

    private Map<HolderType, SQLBuilder> holders = new LinkedHashMap<>() {{
        put(HolderType.INSERT_HOLDER, new InsertHolder());
        put(HolderType.RETURNING_HOLDER, new ReturningHolder());
    }};


    private InsertBuilder() {
    }

    public static InsertBuilder createInsert() {
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
        if (holders.get(HolderType.RETURNING_HOLDER) instanceof ReturningHolder rh) {
            rh.addColumns(columnsEncodings);
        }
        return this;
    }

    @Override
    public String buildSQL() {
        return clearExtraSpaces(complexBuildSQL((SequencedCollection<SQLBuilder>) (holders.values())));
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
        if (holders.get(HolderType.RETURNING_HOLDER) instanceof ReturningHolder rh) {
            var preparedStatement = rh.shouldCreateSQL() ?
                    connection.prepareStatement(buildSQL(), RETURN_GENERATED_KEYS) :
                    connection.prepareStatement(buildSQL());
            fillPreparedStatement(preparedStatement, provideValues());
            return preparedStatement;
        }

        return null;
    }

    @Override
    public List<?> provideValues() {
        return extractAndLinkValues((SequencedCollection<?>) holders.values());
    }
}
