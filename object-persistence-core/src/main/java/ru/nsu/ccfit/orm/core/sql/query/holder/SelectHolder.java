package ru.nsu.ccfit.orm.core.sql.query.holder;

import org.apache.commons.collections4.CollectionUtils;
import ru.nsu.ccfit.orm.core.sql.query.builder.SelectBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Column;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;
import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.enumerateBuildableWithSeparator;
import static ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord.ALL;
import static ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord.SELECT;
import static ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol.COMMA;

public class SelectHolder implements SQLBuilder, ValuesProvider {

    private boolean selectAll = false;
    private List<Column> columns = new LinkedList<>();
    private List<SelectBuilder> selectBuilders = new LinkedList<>();

    public SelectHolder selectAll() {
        selectAll = true;
        return this;
    }

    public SelectHolder addSelect(Object... selectObjects) {
        Arrays.asList(selectObjects).forEach(this::doForObj);
        return this;
    }

    private void doForObj(Object object) {
        switch (object) {
            case String columnMarkdown -> columns.add(Column.fromMarkdown(columnMarkdown));
            case Column column -> columns.add(column);
            case SelectBuilder selectBuilder -> selectBuilders.add(selectBuilder);
            default -> System.out.println("hz");
        }
    }

    @Override
    public String buildSQL() {
        return clearDublicatedSpaces("%s %s".formatted(
                SELECT, selectAll ? ALL : enumerateBuildableWithSeparator(columns, COMMA.getSymbol())
        ));
    }

    @Override
    public boolean shouldCreateSQL() {
        return CollectionUtils.isNotEmpty(columns) || CollectionUtils.isNotEmpty(selectBuilders) || selectAll;
    }

    @Override
    public List<?> provideValues() {
        return List.of();
    }
}
