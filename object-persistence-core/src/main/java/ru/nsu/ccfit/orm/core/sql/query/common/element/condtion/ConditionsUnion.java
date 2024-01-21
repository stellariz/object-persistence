package ru.nsu.ccfit.orm.core.sql.query.common.element.condtion;

import java.util.List;

public record ConditionsUnion(List<ConditionElement> conditions, ConditionBinder conditionBinder)
    implements ConditionSignature {
}
