package ru.nsu.ccfit.orm.core.sql.query.element.condtion;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionFactory;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionSignature;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ConditionGroupTest {

    @ParameterizedTest
    @MethodSource("basicOperations")
    public void testConditionGroup(ConditionSignature conditionSignature, String expectedSQL, List<?> expectedValues) {
        assertEquals(expectedSQL, conditionSignature.buildSQL());
        assertEquals(expectedValues, conditionSignature.provideValues());
    }

    private static Stream<Arguments> basicOperations() {
        return Stream.of(
                Arguments.of(ConditionFactory.equals("{u}name", "TEST"), "u.name = ?", List.of("TEST")),
                Arguments.of(ConditionFactory.notEquals("{u}name", "TEST"), "u.name <> ?", List.of("TEST")),
                Arguments.of(ConditionFactory.like("{u}name", "TEST"), "u.name LIKE ?", List.of("TEST")),
                Arguments.of(ConditionFactory.notLike("{u}name", "TEST"), "u.name NOT LIKE ?", List.of("TEST")),
                Arguments.of(ConditionFactory.isNull("{u}name"), "u.name IS NULL", List.of()),
                Arguments.of(ConditionFactory.isNotNull("{u}name"), "u.name IS NOT NULL", List.of()),
                Arguments.of(ConditionFactory.greaterThan("{u}age", 18), "u.age > ?", List.of(18)),
                Arguments.of(ConditionFactory.greaterThanOrEq("{u}age", 18), "u.age >= ?", List.of(18)),
                Arguments.of(ConditionFactory.lessThan("{u}age", 18), "u.age < ?", List.of(18)),
                Arguments.of(ConditionFactory.lessThanOrEq("{u}age", 18), "u.age <= ?", List.of(18)),
                Arguments.of(ConditionFactory.in("{u}age", List.of(18, 19, 20)), "u.age IN (?, ?, ?)", List.of(18, 19, 20))
        );
    }

    @ParameterizedTest
    @MethodSource("operationsCombinations")
    public void testConditionGroupComplex(ConditionSignature conditionSignature, String expected) {
        assertEquals(expected, conditionSignature.buildSQL());
    }

    private static Stream<Arguments> operationsCombinations() {
        return Stream.of(
                Arguments.of(
                        ConditionFactory.and(
                                ConditionFactory.or(
                                        ConditionFactory.and(
                                                ConditionFactory.equals("{u}name", "TEST"),
                                                ConditionFactory.lessThan("{u}age", 18)
                                        ),
                                        ConditionFactory.and(
                                                ConditionFactory.equals("{u}nickname", "NICK"),
                                                ConditionFactory.greaterThan("{u}age", 20)
                                        )
                                ),
                                ConditionFactory.isNotNull("{u}points")
                        ),
                        "(((u.name = ? AND u.age < ?) OR (u.nickname = ? AND u.age > ?)) AND u.points IS NOT NULL)"
                )
        );
    }

}