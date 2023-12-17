package ru.nsu.ccfit.orm.core.sql.query.builder;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionFactory;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionSignature;
import ru.nsu.ccfit.orm.core.sql.query.common.element.join.Join;
import ru.nsu.ccfit.orm.core.sql.query.common.element.join.JoinFactory;
import ru.nsu.ccfit.orm.core.sql.query.common.element.order.Order;
import ru.nsu.ccfit.orm.core.sql.query.common.element.order.OrderFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectBuilderTest {

    @Test
    public void testSelectAll() {
        SelectBuilder selectBuilder = SelectBuilder.createSelect();

        selectBuilder
                .selectAll()
                .from("users u");

        assertEquals("SELECT * FROM users u", selectBuilder.buildSQL());
    }

    @ParameterizedTest
    @MethodSource("argsForSelectFromCase")
    public void testSelectFrom(List<String> selectQs, String fromQ, String expectedSQL) {
        SelectBuilder selectBuilder = SelectBuilder.createSelect();

        selectBuilder
                .select(Iterables.toArray(selectQs, String.class))
                .from(fromQ);

        assertEquals(expectedSQL, selectBuilder.buildSQL());
    }

    private static Stream<Arguments> argsForSelectFromCase() {
        return Stream.of(
                Arguments.of(List.of("{u}name"), "users u", "SELECT u.name AS u_name FROM users u"),
                Arguments.of(List.of("{u}name{custom_alias}"), "users{u}", "SELECT u.name AS custom_alias FROM users u"),
                Arguments.of(List.of("{u}name{_}"), "users{u}", "SELECT u.name FROM users u"),
                Arguments.of(List.of("{u}*name{_}"), "users{u}", "SELECT DISTINCT u.name FROM users u")
        );
    }

    @ParameterizedTest
    @MethodSource("argsForSelectFromWhereCase")
    public void testSelectFromWhere(
            List<String> selectQs, String fromQ,
            ConditionSignature condSignatureQ,
            String expectedSQL, List<?> expectedValues
    ) {
        SelectBuilder selectBuilder = SelectBuilder.createSelect();

        selectBuilder
                .select(Iterables.toArray(selectQs, String.class))
                .from(fromQ)
                .where(condSignatureQ);

        assertEquals(expectedSQL, selectBuilder.buildSQL());
        assertEquals(expectedValues, selectBuilder.provideValues());
    }

    private static Stream<Arguments> argsForSelectFromWhereCase() {
        return Stream.of(
                Arguments.of(
                        List.of("{u}name"),
                        "users u",
                        ConditionFactory.equals("{u}name", "TEST"),
                        "SELECT u.name AS u_name " +
                                "FROM users u " +
                                "WHERE u.name = ?",
                        List.of("TEST")
                ),
                Arguments.of(
                        List.of("{u}name", "{u}age"),
                        "users u",
                        ConditionFactory.and(
                                ConditionFactory.equals("{u}name", "TEST"),
                                ConditionFactory.equals("{u}age", 18)
                        ),
                        "SELECT u.name AS u_name, u.age AS u_age " +
                                "FROM users u " +
                                "WHERE u.name = ? AND u.age = ?",
                        List.of("TEST", 18)
                ),
                Arguments.of(
                        List.of("{u}name", "{u}age"),
                        "users u",
                        ConditionFactory.or(
                                ConditionFactory.like("{u}name", "TEST"),
                                ConditionFactory.notEquals("{u}age", 18)
                        ),
                        "SELECT u.name AS u_name, u.age AS u_age " +
                                "FROM users u " +
                                "WHERE u.name " +
                                "LIKE ? OR u.age <> ?",
                        List.of("TEST", 18)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("argsForSuperQuery")
    public void testSuperQuery(
            List<String> selectQs,
            String fromQ,
            ConditionSignature condSignatureQ,
            Order orderQ,
            int limitQ,
            List<Join> joinsQ,
            String expectedSQL,
            List<?> expectedValues
    ) {
        SelectBuilder selectBuilder = SelectBuilder.createSelect();

        selectBuilder
                .select(Iterables.toArray(selectQs, String.class))
                .from(fromQ)
                .joins(Iterables.toArray(joinsQ, Join.class))
                .where(condSignatureQ)
                .order(orderQ)
                .limit(limitQ)
                .joins();

        assertEquals(expectedSQL, selectBuilder.buildSQL());
        assertEquals(expectedValues, selectBuilder.provideValues());
    }

    private static Stream<Arguments> argsForSuperQuery() {
        return Stream.of(
                Arguments.of(
                        List.of("{u}name", "{u}age"),
                        "users u",
                        ConditionFactory.or(
                                ConditionFactory.like("{u}name", "TEST"),
                                ConditionFactory.notEquals("{u}age", 18)
                        ),
                        OrderFactory.asc("{u}name"),
                        100,
                        List.of(
                                JoinFactory.createLeftJoin("users_films uf", "{u}id", "{uf}id"),
                                JoinFactory.createRightJoin("users_books ub", "{u}id", "{ub}id"),
                                JoinFactory.createInnerJoin("users_music um", "{u}id", "{um}id")
                        ),
                        "SELECT u.name AS u_name, u.age AS u_age " +
                                "FROM users u " +
                                "LEFT JOIN users_films uf ON u.id = uf.id " +
                                "RIGHT JOIN users_books ub ON u.id = ub.id " +
                                "INNER JOIN users_music um ON u.id = um.id " +
                                "WHERE u.name LIKE ? OR u.age <> ? " +
                                "ORDER BY u.name ASC " +
                                "LIMIT ?",
                        List.of("TEST", 18, 100)
                )
        );
    }

}