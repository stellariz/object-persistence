package ru.nsu.ccfit.orm.core.repository.dsl.selector;

import ru.nsu.ccfit.orm.core.repository.dsl.condition.atomic.*;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.AtomicCondition;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.Condition;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.ConditionLinker;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.linker.AndLinker;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.linker.OrLinker;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionFactory;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionGroup;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionSignature;

import java.util.List;

class ConditionTransformer {
    
    static ConditionSignature convertToConditionSignature(Condition condition) {
        return switch (condition) {
            case ConditionLinker cl -> convertConditionLinker(cl);
            case AtomicCondition ac -> convertAtomicCondition(ac);
            default -> throw new IllegalStateException("Unexpected value: " + condition);
        };
    }
    
    private static ConditionGroup convertConditionLinker(ConditionLinker condition) {
        return switch (condition) {
            case AndLinker(List<Condition> conditions) -> ConditionFactory.and(
                conditions.stream().map(ConditionTransformer::convertToConditionSignature).toList()
            );
            case OrLinker(List<Condition> conditions) -> ConditionFactory.or(
                conditions.stream().map(ConditionTransformer::convertToConditionSignature).toList()
            );
            default -> throw new IllegalStateException("Unexpected value: " + condition);
        };
    }
    
    private static ConditionSignature convertAtomicCondition(AtomicCondition condition) {
        return switch (condition) {
            case Like(String fieldName, String pattern) -> ConditionFactory.like(fieldName, pattern);
            case Equals(String fieldName, Object value) -> ConditionFactory.equals(fieldName, value);
            case NotEquals(String fieldName, Object value) -> ConditionFactory.notEquals(fieldName, value);
            case IsNull(String fieldName) -> ConditionFactory.isNull(fieldName);
            case IsNonNull(String fieldName) -> ConditionFactory.isNotNull(fieldName);
            case IsIn(String fieldName, List<Object> values) -> ConditionFactory.in(fieldName, values);
            case LessThan(String fieldName, Object value) -> ConditionFactory.lessThan(fieldName, value);
            case LessThanOrEq(String fieldName, Object value) -> ConditionFactory.lessThanOrEq(fieldName, value);
            case GreaterThan(String fieldName, Object value) -> ConditionFactory.greaterThan(fieldName, value);
            case GreaterThanOrEq(String fieldName, Object value) -> ConditionFactory.greaterThanOrEq(fieldName, value);
            default -> throw new IllegalStateException("Unexpected value: " + condition);
        };
    }
    
}
