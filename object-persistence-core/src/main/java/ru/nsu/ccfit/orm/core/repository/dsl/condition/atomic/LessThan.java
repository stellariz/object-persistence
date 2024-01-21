package ru.nsu.ccfit.orm.core.repository.dsl.condition.atomic;

import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.AtomicCondition;

public record LessThan(String fieldName, Object value) implements AtomicCondition {
}
