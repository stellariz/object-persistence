package ru.nsu.ccfit.orm.core.sql.query.holder;

import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionGroup;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionSignature;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;

import java.util.List;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;

public class ConditionsHolder implements SQLBuilder, ValuesProvider {

    private ConditionGroup baseConditionGroup = new ConditionGroup();

    public void addCondition(ConditionSignature conditionSignature) {
        baseConditionGroup.add(conditionSignature);
        baseConditionGroup.generateMetadata();
    }

    @Override
    public String buildSQL() {
        return clearDublicatedSpaces("%s %s".formatted(KeyWord.WHERE, baseConditionGroup.buildSQL()));
    }

    @Override
    public boolean shouldCreateSQL() {
        return CollectionUtils.isNotEmpty(baseConditionGroup.getConnectedConditions());
    }

    @Override
    public List<?> provideValues() {
        return baseConditionGroup.provideValues();
    }
}
