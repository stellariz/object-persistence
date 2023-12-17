package ru.nsu.ccfit.orm.core.sql.query.holder;

import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.order.Order;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearExtraSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.enumerateBuildableWithSeparator;

public class OrderHolder implements SQLBuilder {
    private List<Order> orders = new ArrayList<>();

    public OrderHolder addOrder(Order order) {
        orders.add(order);
        return this;
    }

    @Override
    public String buildSQL() {
        return clearExtraSpaces("ORDER BY %s".formatted(
                enumerateBuildableWithSeparator(orders, Symbol.COMMA.getSymbol())
        ));
    }

    @Override
    public boolean shouldCreateSQL() {
        return CollectionUtils.isNotEmpty(orders);
    }
}
