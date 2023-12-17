package ru.nsu.ccfit.orm.core.sql.query.element;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Column;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Table;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    @ParameterizedTest
    @MethodSource("getArguments")
    public void testMarkdownDecoding(
            String name, String alias, String markdown, boolean isInvalidEncoding
    ) {
        if (isInvalidEncoding) {
            assertThrows(IllegalArgumentException.class, () -> Column.fromMarkdown(markdown));
            return;
        }

        Table table = Table.fromMarkdown(markdown);
        assertEquals(name, table.name());
        assertEquals(alias, table.alias());
    }

    private static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of("users", "u", "users{u}", false),
                Arguments.of("users", null, "users{_}", false),
                Arguments.of("users", "u", "users u", false),
                Arguments.of("users", null, "users", false),
                Arguments.of(null, null, "[users]", true)
        );
    }

}