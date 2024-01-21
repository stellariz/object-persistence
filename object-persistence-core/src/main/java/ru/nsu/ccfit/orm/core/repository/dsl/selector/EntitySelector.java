package ru.nsu.ccfit.orm.core.repository.dsl.selector;

import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.meta.manager.EntitySearchProvider;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.atomic.*;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.common.Condition;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.linker.AndLinker;
import ru.nsu.ccfit.orm.core.repository.dsl.condition.linker.OrLinker;
import ru.nsu.ccfit.orm.core.sql.query.builder.SelectBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.element.order.OrderFactory;
import ru.nsu.ccfit.orm.model.common.OrderType;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ru.nsu.ccfit.orm.core.repository.dsl.selector.ConditionTransformer.convertToConditionSignature;

/**
 * Allows to get objects by using own SQL builders AND objects metadata.
 */
public class EntitySelector<T> {
    
    private final SelectBuilder selectBuilder = SelectBuilder.createSelect().selectAll();
    private final TableMetaData tableMetaData;
    private final EntitySearchProvider entitySearchProvider;
    private final Class<T> forClass;
    
    public EntitySelector(TableMetaData tableMetaData, EntitySearchProvider entitySearchProvider, Class<T> forClass) {
        this.tableMetaData = tableMetaData;
        this.entitySearchProvider = entitySearchProvider;
        this.forClass = forClass;
        
        selectBuilder.from(tableMetaData.tableName());
    }
    
    public EntitySelector<T> where(Condition condition) {
        selectBuilder.where(convertToConditionSignature(condition));
        return this;
    }
    
    public EntitySelector<T> limit(int limit) {
        selectBuilder.limit(limit);
        return this;
    }
    
    public EntitySelector<T> orderBy(String fieldName, OrderType orderType) {
        selectBuilder.order(
            switch (orderType) {
                case ASCENDING -> OrderFactory.asc(fieldName);
                case DESCENDING -> OrderFactory.desc(fieldName);
            }
        );
        return this;
    }
    
    public List<? extends T> toList() {
        return entitySearchProvider.searchByQuery(selectBuilder, tableMetaData, forClass);
    }
    
    public Optional<? extends T> findFirst() {
        var list = entitySearchProvider.searchByQuery(selectBuilder, tableMetaData, forClass);
        return CollectionUtils.isEmpty(list) ? Optional.empty() : list.stream().findFirst();
    }
    
    public static AndLinker and(Condition... conditions) {
        return new AndLinker(Arrays.asList(conditions));
    }
    
    public static OrLinker or(Condition... conditions) {
        return new OrLinker(Arrays.asList(conditions));
    }
    
    public static Like like(String fieldName, String pattern) {
        return new Like(fieldName, pattern);
    }
    
    public static Equals equal(String fieldName, Object value) {
        return new Equals(fieldName, value);
    }
    
    public static IsNull isNull(String fieldName) {
        return new IsNull(fieldName);
    }
    
    
    public static IsNonNull isNonNull(String fieldName) {
        return new IsNonNull(fieldName);
    }
    
    public static IsIn in(String fieldName, List<Object> values) {
        return new IsIn(fieldName, values);
    }
    
    public static GreaterThan greaterThan(String fieldName, Object value) {
        return new GreaterThan(fieldName, value);
    }
    
    public static GreaterThanOrEq greaterThanOrEq(String fieldName, Object value) {
        return new GreaterThanOrEq(fieldName, value);
    }
    
    public static LessThan lessThan(String fieldName, Object value) {
        return new LessThan(fieldName, value);
    }
    
    public static LessThanOrEq lessThanOrEq(String fieldName, Object value) {
        return new LessThanOrEq(fieldName, value);
    }
    
}
