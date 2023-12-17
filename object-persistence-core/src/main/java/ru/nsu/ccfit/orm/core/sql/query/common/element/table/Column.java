package ru.nsu.ccfit.orm.core.sql.query.common.element.table;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearExtraSpaces;

@Builder
public record Column(String name, String alias, String prefix, boolean isDistinct) implements SQLBuilder {

    public Column(String name, boolean isDistinct) {
        this(name, null, null, isDistinct);
    }

    public Column(String name, String prefix, boolean isDistinct) {
        this(name, "%s_%s".formatted(prefix, name), prefix, isDistinct);
    }

    public String sqlRepresentation() {
        return StringUtils.isNotEmpty(prefix) ? "%s.%s".formatted(prefix, name) : name;
    }

    @Override
    public String buildSQL() {
        StringBuilder result = new StringBuilder();

        if (isDistinct) {
            result.append("DISTINCT ");
        }

        if (Objects.nonNull(prefix)) {
            result.append(prefix).append(".");
        }

        result.append(name);

        if (Objects.nonNull(alias)) {
            result.append(" AS %s".formatted(alias));
        }

        return clearExtraSpaces(result.toString());
    }

    public static Column fromMarkdown(String columnEncoding) {
        var markdownWithTableAliasPattern = Pattern.compile("\\{(\\w+)\\}(\\**\\w+)");
        var markdownWithTableAliasMatcher = markdownWithTableAliasPattern.matcher(columnEncoding);
        if (markdownWithTableAliasMatcher.matches()) {
            var prefix = markdownWithTableAliasMatcher.group(1);
            var name = markdownWithTableAliasMatcher.group(2);
            var distinct = false;

            if (name.contains("*")) {
                name = name.replace("*", "");
                distinct = true;
            }

            return Symbol.isUnderline(prefix) ? new Column(name, distinct) : new Column(name, prefix, distinct);
        }

        var fullMarkdownPattern = Pattern.compile("\\{(\\w+)\\}(\\**\\w+)\\{(\\w+)\\}");
        var fullMarkdownMatcher = fullMarkdownPattern.matcher(columnEncoding);
        if (fullMarkdownMatcher.matches()) {
            var prefix = fullMarkdownMatcher.group(1);
            var name = fullMarkdownMatcher.group(2);
            var customAlias = fullMarkdownMatcher.group(3);
            var distinct = false;

            prefix = Symbol.isUnderline(prefix) ? null : prefix;
            customAlias = Symbol.isUnderline(customAlias) ? null : customAlias;

            if (name.contains("*")) {
                name = name.replace("*", "");
                distinct = true;
            }

            return new Column(name, customAlias, prefix, distinct);
        }

        var simpleMarkdownPattern = Pattern.compile("\\w+");
        var simpleMarkdownMatcher = simpleMarkdownPattern.matcher(columnEncoding);
        if (simpleMarkdownMatcher.matches()) {
            return new Column(columnEncoding, false);
        }

        throw new IllegalArgumentException("Invalid column markdown format");
    }

    public static List<Column> fromMarkdowns(String... columnEncodings) {
        return Arrays.stream(columnEncodings).map(Column::fromMarkdown).toList();
    }

}
