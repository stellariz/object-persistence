package ru.nsu.ccfit.orm.core.sql.query.common.element.order;

import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;

public class OrderFactory {

    public static Order by(String columnEncoding, KeyWord type) {
        return new Order(columnEncoding, type);
    }

    public static Order asc(String columnEncoding) {
        return new Order(columnEncoding, KeyWord.ASC);
    }

    public static Order desc(String columnEncoding) {
        return new Order(columnEncoding, KeyWord.DESC);
    }

}
