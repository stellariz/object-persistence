package ru.nsu.ccfit.orm.core.sql.query.builder;

import ru.nsu.ccfit.orm.core.sql.query.common.PreparedStatementBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionSignature;
import ru.nsu.ccfit.orm.core.sql.query.common.element.join.Join;
import ru.nsu.ccfit.orm.core.sql.query.common.element.order.Order;
import ru.nsu.ccfit.orm.core.sql.query.holder.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;

public class SelectBuilder implements SQLBuilder, PreparedStatementBuilder, ValuesProvider {

    /* Also it present sequence order. */
    private Map<HolderType, SQLBuilder> holders = new LinkedHashMap<>() {{
        put(HolderType.SELECT_HOLDER, new SelectHolder());
        put(HolderType.FROM_HOLDER, new FromHolder());
        put(HolderType.JOIN_HOLDER, new JoinHolder());
        put(HolderType.CONDITIONS_HOLDER, new ConditionsHolder());
        put(HolderType.ORDER_HOLDER, new OrderHolder());
        put(HolderType.LIMIT_HOLDER, new LimitHolder());
    }};

    private SelectBuilder() {
    }

    public static SelectBuilder createSelect() {
        return new SelectBuilder();
    }

    public SelectBuilder select(Object... fields) {
        if (holders.get(HolderType.SELECT_HOLDER) instanceof SelectHolder sh) {
            sh.addSelect(fields);
        }
        return this;
    }

    public SelectBuilder selectAll() {
        if (holders.get(HolderType.SELECT_HOLDER) instanceof SelectHolder sh) {
            sh.selectAll();
        }
        return this;
    }

    public SelectBuilder from(String... fromTables) {
        if (holders.get(HolderType.FROM_HOLDER) instanceof FromHolder fh) {
            fh.addTables(fromTables);
        }
        return this;
    }

    public SelectBuilder where(ConditionSignature... conditionSignatures) {
        if (holders.get(HolderType.CONDITIONS_HOLDER) instanceof ConditionsHolder ch) {
            Arrays.stream(conditionSignatures).forEach(ch::addCondition);
        }
        return this;
    }

    public SelectBuilder order(Order order) {
        if (holders.get(HolderType.ORDER_HOLDER) instanceof OrderHolder oh) {
            oh.addOrder(order);
        }
        return this;
    }

    public SelectBuilder limit(int limit) {
        if (holders.get(HolderType.LIMIT_HOLDER) instanceof LimitHolder lh) {
            lh.setLimit(limit);
        }
        return this;
    }

    public SelectBuilder join(Join join) {
        if (holders.get(HolderType.JOIN_HOLDER) instanceof JoinHolder jh) {
            jh.addJoin(join);
        }
        return this;
    }

    public SelectBuilder joins(Join... joins) {
        if (holders.get(HolderType.JOIN_HOLDER) instanceof JoinHolder jh) {
            jh.addJoins(joins);
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
        if (holders.get(HolderType.SELECT_HOLDER) instanceof SelectHolder sh) {
            return sh.shouldCreateSQL();
        }
        return false;
    }

    @Override
    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        var preparedStatement = connection.prepareStatement(buildSQL());
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
