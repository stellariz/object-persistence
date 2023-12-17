package ru.nsu.ccfit.orm.core.sql.query.builder;

import ru.nsu.ccfit.orm.core.sql.query.common.PreparedStatementBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionSignature;
import ru.nsu.ccfit.orm.core.sql.query.holder.ConditionsHolder;
import ru.nsu.ccfit.orm.core.sql.query.holder.HolderType;
import ru.nsu.ccfit.orm.core.sql.query.holder.ReturningHolder;
import ru.nsu.ccfit.orm.core.sql.query.holder.UpdateHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.fillPreparedStatement;

public class UpdateBuilder implements SQLBuilder, PreparedStatementBuilder, ValuesProvider {

    private Map<HolderType, SQLBuilder> holders = new LinkedHashMap<>() {{
        put(HolderType.UPDATE_HOLDER, new UpdateHolder());
        put(HolderType.CONDITIONS_HOLDER, new ConditionsHolder());
        put(HolderType.RETURNING_HOLDER, new ReturningHolder());
    }};

    private UpdateBuilder() {
    }

    public static UpdateBuilder createUpdate() {
        return new UpdateBuilder();
    }

    public UpdateBuilder table(String tableEncoding) {
        if (holders.get(HolderType.UPDATE_HOLDER) instanceof UpdateHolder uh) {
            uh.table(tableEncoding);
        }
        return this;
    }

    public UpdateBuilder updateSet(Map<String, ?> updateSet) {
        if (holders.get(HolderType.UPDATE_HOLDER) instanceof UpdateHolder uh) {
            uh.updateSet(updateSet);
        }
        return this;
    }

    public UpdateBuilder where(ConditionSignature... conditionSignatures) {
        if (holders.get(HolderType.CONDITIONS_HOLDER) instanceof ConditionsHolder ch) {
            Arrays.stream(conditionSignatures).forEach(ch::addCondition);
        }
        return this;
    }

    public UpdateBuilder returning(String... columnsEncodings) {
        if (holders.get(HolderType.RETURNING_HOLDER) instanceof ReturningHolder rh) {
            rh.addColumns(columnsEncodings);
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
        if (holders.get(HolderType.UPDATE_HOLDER) instanceof UpdateHolder uh) {
            return uh.shouldCreateSQL();
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
