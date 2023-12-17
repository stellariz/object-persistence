package ru.nsu.ccfit.orm.core.sql.query.common.element;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;

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
            result.append(KeyWord.DISTINCT)
                    .append(Symbol.SPACE);
        }

        if (prefix != null) {
            result.append(prefix).append(".");
        }

        result.append(name);

        if (alias != null) {
            result.append(Symbol.SPACE)
                    .append(KeyWord.AS)
                    .append(Symbol.SPACE)
                    .append(alias);
        }

        return clearDublicatedSpaces(result.toString());
    }

    public static Column fromMarkdown(String columnEncoding) {
        Pattern markdownWithTableAlias = Pattern.compile("\\{(\\w+)\\}(\\**\\w+)");
        Pattern fullMarkdown = Pattern.compile("\\{(\\w+)\\}(\\**\\w+)\\{(\\w+)\\}");
        Pattern simpleMarkdown = Pattern.compile("\\w+");

        Matcher markdownWithTableAliasMatcher = markdownWithTableAlias.matcher(columnEncoding);
        Matcher fullMarkdownMatcher = fullMarkdown.matcher(columnEncoding);
        Matcher simpleMarkdownMatcher = simpleMarkdown.matcher(columnEncoding);

        if (markdownWithTableAliasMatcher.matches()) {
            var prefix = markdownWithTableAliasMatcher.group(1);
            var name = markdownWithTableAliasMatcher.group(2);
            var distinct = false;

            if (name.contains("*")) {
                name = name.replace("*", "");
                distinct = true;
            }

            return Symbol.isUnderline(prefix) ? new Column(name, distinct) : new Column(name, prefix, distinct);
        } else if (fullMarkdownMatcher.matches()) {
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
        } else if (simpleMarkdownMatcher.matches()) {
            return new Column(columnEncoding, false);
        } else {
            throw new IllegalArgumentException("Invalid column markdown format");
        }
    }

    public static List<Column> fromMarkdowns(String... columnEncodings) {
        return Arrays.stream(columnEncodings).map(Column::fromMarkdown).toList();
    }

}
