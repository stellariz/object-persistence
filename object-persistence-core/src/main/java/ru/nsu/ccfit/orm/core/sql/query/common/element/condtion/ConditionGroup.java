package ru.nsu.ccfit.orm.core.sql.query.common.element.condtion;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;

@Getter
public class ConditionGroup implements SQLBuilder, ConditionSignature, ValuesProvider {

    @Setter
    private boolean nested = false;
    private List<Object> connectedConditions = new ArrayList<>();

    public void add(ConditionSignature condition) {
        connectedConditions.add(condition);
    }

    void add(ConditionBinder binder, ConditionSignature condition) {
        if (CollectionUtils.isEmpty(connectedConditions)) {
            connectedConditions.add(condition);
        } else {
            connectedConditions.add(binder);
            connectedConditions.add(condition);
        }
    }

    public void generateMetadata() {
        connectedConditions.forEach((item) -> {
            if (item instanceof ConditionGroup cg && cg != connectedConditions.get(0)) {
                cg.setNested(true);
                cg.generateMetadata();
            }
        });
    }

    @Override
    public String buildSQL() {
        String result = connectedConditions.stream()
                .map(this::getString)
                .collect(Collectors.joining(""));
        return clearDublicatedSpaces(result);
    }

    private String getString(Object object) {
        return switch (object) {
            case ConditionBinder conditionBinder -> " %s ".formatted(conditionBinder.getKeyword());
            case ConditionElement conditionElement -> conditionElement.buildSQL();
            case ConditionGroup conditionGroup -> conditionGroup.nested ?
                    "(%s)".formatted(conditionGroup.buildSQL()) : conditionGroup.buildSQL();
            default -> "";
        };
    }

    @Override
    public List<?> provideValues() {
        return connectedConditions.stream()
                .filter(s -> s instanceof ValuesProvider)
                .map(s -> (ValuesProvider) s)
                .flatMap(s -> s.provideValues().stream())
                .toList();
    }
}
