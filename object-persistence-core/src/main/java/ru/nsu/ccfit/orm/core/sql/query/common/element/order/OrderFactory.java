package ru.nsu.ccfit.orm.core.sql.query.common.element.order;

import ru.nsu.ccfit.orm.model.common.OrderType;

public class OrderFactory {

    public static Order by(String columnEncoding, OrderType type) {
        return new Order(columnEncoding, type);
    }

    public static Order asc(String columnEncoding) {
        return new Order(columnEncoding, OrderType.ASCENDING);
    }

    public static Order desc(String columnEncoding) {
        return new Order(columnEncoding, OrderType.DESCENDING);
    }

}
