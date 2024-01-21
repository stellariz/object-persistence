package ru.nsu.ccfit.orm.core.sql.query.holder;

import lombok.Setter;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionSignature;

import java.util.List;
import java.util.Objects;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearExtraSpaces;

@Setter
public class ConditionsHolder implements SQLBuilder, ValuesProvider {
    
    private ConditionSignature conditionSignature;
    
    @Override
    public String buildSQL() {
        return clearExtraSpaces("WHERE %s".formatted(conditionSignature.buildSQL()));
    }
    
    @Override
    public boolean shouldCreateSQL() {
        return Objects.nonNull(conditionSignature);
    }
    
    @Override
    public List<?> provideValues() {
        return conditionSignature.provideValues();
    }
}
