package ru.nsu.ccfit.orm.core.sql.query.common.element.condtion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum ConditionBinder {
    OR("OR"),
    AND("AND");

    private final String keyword;
}
