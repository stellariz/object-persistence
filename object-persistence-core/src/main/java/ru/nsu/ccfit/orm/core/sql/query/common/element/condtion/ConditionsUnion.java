package ru.nsu.ccfit.orm.core.sql.query.common.element.condtion;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public record ConditionsUnion(List<ConditionSignature> conditions, ConditionBinder conditionBinder)
    implements ConditionSignature {
    @Override
    public String buildSQL() {
        var body = conditions.stream()
            .map(SQLBuilder::buildSQL)
            .collect(Collectors.joining(" %s ".formatted(conditionBinder.getKeyword())));
        return "(%s)".formatted(body);
    }
    
    @Override
    public List<?> provideValues() {
        return conditions.stream()
            .map(ConditionSignature::provideValues)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }
}
