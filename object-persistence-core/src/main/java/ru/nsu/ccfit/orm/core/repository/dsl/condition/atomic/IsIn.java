package ru.nsu.ccfit.orm.core.repository.dsl.condition.atomic;

import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.AtomicCondition;

import java.util.List;

public record IsIn(String fieldName, List<Object> values) implements AtomicCondition {
}
