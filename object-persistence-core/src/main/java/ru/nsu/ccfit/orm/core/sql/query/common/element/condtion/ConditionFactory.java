package ru.nsu.ccfit.orm.core.sql.query.common.element.condtion;

import ru.nsu.ccfit.orm.core.sql.query.common.element.table.Column;

import java.util.Arrays;
import java.util.List;

public class ConditionFactory {
    
    public static ConditionsUnion and(List<ConditionSignature> conditions) {
        return new ConditionsUnion(conditions, ConditionBinder.AND);
    }
    
    public static ConditionsUnion and(ConditionSignature... conditions) {
        return new ConditionsUnion(Arrays.stream(conditions).toList(), ConditionBinder.AND);
    }
    
    public static ConditionsUnion or(List<ConditionSignature> conditions) {
        return new ConditionsUnion(conditions, ConditionBinder.OR);
    }
    
    public static ConditionsUnion or(ConditionSignature... conditions) {
        return new ConditionsUnion(Arrays.stream(conditions).toList(), ConditionBinder.OR);
    }
    
    public static ConditionSignature equals(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.EQUALS, value);
    }
    
    public static ConditionSignature notEquals(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.NOT_EQUALS, value);
    }
    
    public static ConditionSignature in(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.IN, value);
    }
    
    public static ConditionSignature isNull(String field) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.IS_NULL, null);
    }
    
    public static ConditionSignature isNotNull(String field) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.IS_NOT_NULL, null);
    }
    
    public static ConditionSignature like(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.LIKE, value);
    }
    
    public static ConditionSignature notLike(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.NOT_LIKE, value);
    }
    
    public static ConditionSignature greaterThan(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.GREATER_THAN, value);
    }
    
    public static ConditionSignature greaterThanOrEq(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.GREATER_THAN_OR_EQUAL, value);
    }
    
    public static ConditionSignature lessThan(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.LESS_THAN, value);
    }
    
    public static ConditionSignature lessThanOrEq(String field, Object value) {
        return new ConditionElement(Column.fromMarkdown(field), Comparison.LESS_THAN_OR_EQUAL, value);
    }
    
}
