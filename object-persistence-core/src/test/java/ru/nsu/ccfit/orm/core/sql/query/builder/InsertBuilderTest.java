package ru.nsu.ccfit.orm.core.sql.query.builder;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsertBuilderTest {

    @Test
    public void testBasicInsertSQL(
    ) {
        InsertBuilder insertBuilder = InsertBuilder.createInsert();

        Map<String, ?> insertSet = new LinkedHashMap<>() {{
            put("name", "Roman");
            put("age", 22);
        }};

        insertBuilder
                .table("users")
                .insertSet(insertSet)
                .returning("id");

        assertEquals(
                "INSERT INTO users (name, age) VALUES (?, ?) RETURNING id",
                insertBuilder.buildSQL()
        );

        assertEquals(
                List.of("Roman", 22),
                insertBuilder.provideValues()
        );
    }

}