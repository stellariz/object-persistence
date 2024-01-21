package ru.nsu.ccfit.orm.core.repository.dsl.condition.linker;

import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.Condition;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.ConditionLinker;

import java.util.List;

public record AndLinker(List<Condition> conditions) implements ConditionLinker {
    public AndLinker(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
