package ru.nsu.ccfit.orm.core.sql.query.common.element.join;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JoinType {

    LEFT_JOIN("LEFT JOIN"),
    RIGHT_JOIN("RIGHT JOIN"),
    INNER_JOIN("INNER JOIN");

    private String value;

    @Override
    public String toString() {
        return value;
    }
}
