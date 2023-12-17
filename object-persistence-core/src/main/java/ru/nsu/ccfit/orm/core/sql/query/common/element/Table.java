package ru.nsu.ccfit.orm.core.sql.query.common.element;

import org.apache.commons.lang3.StringUtils;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.nsu.ccfit.orm.core.sql.query.builder.BuilderUtils.clearDublicatedSpaces;

public record Table(String name, String alias) implements SQLBuilder {

    @Override
    public String buildSQL() {
        return clearDublicatedSpaces(StringUtils.isNotEmpty(alias) ? "%s %s".formatted(name, alias) : name);
    }

    public static Table fromMarkdown(String tableEncoding) {
        Pattern patternMarkdown = Pattern.compile("(\\w+)\\{(\\w+)\\}");
        Pattern patternPlain = Pattern.compile("(\\w+)(\\s(\\w+))*");

        Matcher matcherMarkdown = patternMarkdown.matcher(tableEncoding);
        Matcher matcherPlain = patternPlain.matcher(tableEncoding);

        if (matcherMarkdown.matches()) {
            String name = matcherMarkdown.group(1);
            String alias = matcherMarkdown.group(2);
            alias = Symbol.isUnderline(alias) ? null : alias;

            return new Table(name, alias);
        } else if (matcherPlain.matches()) {
            String name = matcherPlain.group(1);
            String alias = matcherPlain.group(3);

            return new Table(name, alias);
        } else {
            throw new IllegalArgumentException("Invalid table markdown format");
        }
    }

    public static List<Table> fromMarkdowns(String... tableEncodings) {
        return Arrays.stream(tableEncodings).map(Table::fromMarkdown).toList();
    }

}
