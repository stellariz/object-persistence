package ru.nsu.ccfit.orm.core.sql.query.holder;

import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.element.join.Join;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearExtraSpaces;

public class JoinHolder implements SQLBuilder {

    private List<Join> joins = new ArrayList<>();

    public JoinHolder addJoin(Join join) {
        joins.add(join);
        return this;
    }

    public JoinHolder addJoins(Join... joins) {
        this.joins.addAll(List.of(joins));
        return this;
    }

    @Override
    public String buildSQL() {
        return clearExtraSpaces(joins.stream().map(Join::buildSQL).collect(Collectors.joining(" ")));
    }

    @Override
    public boolean shouldCreateSQL() {
        return CollectionUtils.isNotEmpty(joins);
    }

}
