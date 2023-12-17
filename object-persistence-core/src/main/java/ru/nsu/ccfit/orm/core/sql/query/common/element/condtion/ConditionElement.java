package ru.nsu.ccfit.orm.core.sql.query.common.element.condtion;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.builder.SelectBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Column;

import java.util.List;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.enumerateVariablesWithSeparator;

public record ConditionElement(Column column, Comparison comparison, Object value) implements
        SQLBuilder, ConditionSignature, ValuesProvider {

    public String getValueAsString() {
        return switch (value) {
            case SelectBuilder sl -> "(%s)".formatted(sl.buildSQL());
            case Column col -> col.name();
            case List list -> "(%s)".formatted(enumerateVariablesWithSeparator(list.size(), Symbol.COMMA.getSymbol()));
            case null -> "";
            default -> Symbol.VARIABLE.getSymbol();
        };
    }

    @Override
    public String buildSQL() {
        return clearDublicatedSpaces("%s %s %s".formatted(
                column.sqlRepresentation(), comparison.getValue(), getValueAsString()
        ));
    }

    @Override
    public List<?> provideValues() {
        if (value == null) {
            return List.of();
        }
        return value instanceof List<?> l ? l : List.of(value);
    }
}
