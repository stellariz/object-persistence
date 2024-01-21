package ru.nsu.ccfit.orm.core.repository.dsl.condition.atomic;

import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.AtomicCondition;

public record GreaterThanOrEq(String fieldName, Object value) implements AtomicCondition {
}
