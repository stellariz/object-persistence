package ru.nsu.ccfit.orm.core.sql.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TemporaryQueryBuilderTest {

    QueryBuilder uut = new TemporaryQueryBuilder();

    @Test
    void test() throws NoSuchFieldException, NoSuchMethodException {
        var actualResult = uut.buildSqlCreateTableQuery(QueryTestUtils.buildRichClassMetaData());
        var expectedResult = """
                CREATE TABLE IF NOT EXISTS TEST_TABLE (
                    idField INTEGER PRIMARY KEY, 
                    stringField VARCHAR(255) , 
                    intField INTEGER , 
                    doubleField DOUBLE PRECISION , 
                    floatField DOUBLE PRECISION , 
                    bigDecimalField NUMERIC , 
                    bigIntegerField BIGINT , 
                    booleanField BIT , 
                    dateField DATE 
                    )
                    """;
        Assertions.assertEquals(cleanString(expectedResult), cleanString(actualResult));
    }

    private String cleanString(String rawString) {
        return rawString.replaceAll("[^a-zA-Z0-9(),]", "");
    }

}