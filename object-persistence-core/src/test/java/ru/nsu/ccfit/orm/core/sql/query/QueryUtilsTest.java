package ru.nsu.ccfit.orm.core.sql.query;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryUtilsTest {

    @Test
    void checkSelectById() throws NoSuchFieldException, NoSuchMethodException {
        var queryActual = QueryUtils.buildFindByIdQuery(QueryTestUtils.buildRichClassMetaData());
        var queryExpected = "SELECT * FROM TEST_TABLE T WHERE T.idField = ?";

        assertEquals(queryExpected, queryActual);
    }

    @Test
    void checkSelectAll() throws NoSuchFieldException, NoSuchMethodException {
        var queryActual = QueryUtils.buildFindAllSimpleQuery(QueryTestUtils.buildRichClassMetaData());
        var queryExpected = "SELECT * FROM TEST_TABLE T";

        assertEquals(queryExpected, queryActual);
    }

    @Test
    void checkInsertAll() throws NoSuchFieldException, NoSuchMethodException {
        var queryActual = QueryUtils.buildInsertQuery(
                QueryTestUtils.buildRichClassMetaData(),
                new LinkedHashSet<>() {
                    {
                        add("idField");
                        add("stringField");
                        add("intField");
                        add("bigDecimalField");
                        add("bigIntegerField");
                        add("doubleField");
                    }
                }
        );
        var queryExpected = "INSERT INTO " +
                "TEST_TABLE (idField, stringField, intField, bigDecimalField, bigIntegerField, doubleField) " +
                "VALUES (?,?,?,?,?,?) RETURNING idField";

        assertEquals(queryExpected, queryActual);
    }

    @Test
    void checkUpdateAll() throws NoSuchFieldException, NoSuchMethodException {
        var queryActual = QueryUtils.buildUpdateQuery(
                QueryTestUtils.buildRichClassMetaData(),
                new LinkedHashSet<>() {
                    {
                        add("idField");
                        add("stringField");
                        add("intField");
                        add("bigDecimalField");
                        add("bigIntegerField");
                        add("doubleField");
                    }
                }
        );
        var queryExpected = "UPDATE TEST_TABLE SET " +
                "idField = ?, stringField = ?, intField = ?, bigDecimalField = ?, bigIntegerField = ?, doubleField = ? " +
                "WHERE T.idField = ? " +
                "RETURNING idField";

        assertEquals(queryExpected, queryActual);
    }

}