package ru.nsu.ccfit.orm.core.sql.query.common.element.condtion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum Comparison {
    EQUALS("=="),
    NOT_EQUALS("<>"),
    IN("IN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    LIKE("LIKE"),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN_OR_EQUAL("<="),
    NOT_LIKE("NOT LIKE");
    private final String value;
}
