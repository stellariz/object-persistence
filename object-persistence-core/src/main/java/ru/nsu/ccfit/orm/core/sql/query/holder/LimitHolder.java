package ru.nsu.ccfit.orm.core.sql.query.holder;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Limit;

import java.util.List;
import java.util.Objects;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearExtraSpaces;

public class LimitHolder implements SQLBuilder, ValuesProvider {
    private Limit limit;

    public LimitHolder setLimit(int value) {
        limit = new Limit(value);
        return this;
    }

    @Override
    public String buildSQL() {
        return clearExtraSpaces("LIMIT ?");
    }

    @Override
    public boolean shouldCreateSQL() {
        return limit != null;
    }

    @Override
    public List<?> provideValues() {
        return Objects.isNull(limit) ? List.of() : List.of(limit.value());
    }
}