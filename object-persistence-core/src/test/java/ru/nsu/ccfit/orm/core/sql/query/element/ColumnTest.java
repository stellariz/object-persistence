package ru.nsu.ccfit.orm.core.sql.query.element;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.ccfit.orm.core.sql.query.common.element.Column;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColumnTest {

    @ParameterizedTest
    @MethodSource("getArguments")
    public void testMarkdownDecoding(
            String name, String prefix, String alias, boolean isDistinct, String markdown, boolean isInvalidEncoding
    ) {
        if (isInvalidEncoding) {
            assertThrows(IllegalArgumentException.class, () -> Column.fromMarkdown(markdown));
            return;
        }

        Column column = Column.fromMarkdown(markdown);
        assertEquals(name, column.name());
        assertEquals(prefix, column.prefix());
        assertEquals(alias, column.alias());
        assertEquals(isDistinct, column.isDistinct());
    }

    private static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of("name", "u", "u_name", false, "{u}name", false),
                Arguments.of("name", "u", "custom_alias", false, "{u}name{custom_alias}", false),
                Arguments.of("name", "u", null, false, "{u}name{_}", false),
                Arguments.of("name", "u", "u_name", true, "{u}*name", false),
                Arguments.of("name", null, null, false, "{_}name", false),
                Arguments.of("name", null, null, false, "name", false),
                Arguments.of("name", null, "custom_name", false, "{_}name{custom_name}", false)
        );
    }

}