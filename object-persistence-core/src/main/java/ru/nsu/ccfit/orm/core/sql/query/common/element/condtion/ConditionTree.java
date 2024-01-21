package ru.nsu.ccfit.orm.core.sql.query.common.element.condtion;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;

public record ConditionTree(ConditionNode head) implements SQLBuilder {
    
    public void addCondition(ConditionSignature conditionSignature) {
    }
    
    @Override
    public String buildSQL() {
        return null;
    }
    
    @Override
    public boolean shouldCreateSQL() {
        return head != null;
    }
    
    private record ConditionNode(ConditionSignature conditionsUnion, ConditionNode left, ConditionNode right) {
        public ConditionNode(ConditionSignature conditionSignature) {
            this(conditionSignature, null, null);
        }
    }
    
}
