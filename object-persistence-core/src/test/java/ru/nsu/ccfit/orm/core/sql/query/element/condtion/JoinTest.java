package ru.nsu.ccfit.orm.core.sql.query.element.condtion;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.ccfit.orm.core.sql.query.common.element.join.Join;
import ru.nsu.ccfit.orm.core.sql.query.common.element.join.JoinFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JoinTest {

    @ParameterizedTest
    @MethodSource("basicOperations")
    public void testConditionGroup(Join join, String expectedSQL) {
        assertEquals(join.buildSQL(), expectedSQL);
    }

    private static Stream<Arguments> basicOperations() {
        return Stream.of(
                Arguments.of(
                        JoinFactory.createLeftJoin("users_films uf", "{u}id", "{uf}id"),
                        "LEFT JOIN users_films uf ON u.id = uf.id"
                ),
                Arguments.of(
                        JoinFactory.createRightJoin("users_films uf", "{u}id", "{uf}id"),
                        "RIGHT JOIN users_films uf ON u.id = uf.id"
                ),
                Arguments.of(
                        JoinFactory.createInnerJoin("users_films uf", "{u}id", "{uf}id"),
                        "INNER JOIN users_films uf ON u.id = uf.id"
                )
        );
    }

}