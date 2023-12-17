package ru.nsu.ccfit.orm.core.sql.query.builder;

import org.junit.jupiter.api.Test;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateBuilderTest {

    @Test
    public void testBasicUpdateSQL(
    ) {
        UpdateBuilder updateBuilder = UpdateBuilder.createUpdate();

        Map<String, ?> updateSet = new LinkedHashMap<>() {{
            put("name", "Roman");
            put("age", 22);
        }};

        updateBuilder
                .table("users u")
                .updateSet(updateSet)
                .where(ConditionFactory.equals("{u}id", 20))
                .returning("id");

        assertEquals(
                "UPDATE users u SET name = ?, age = ? " +
                        "WHERE u.id = ? " +
                        "RETURNING id",
                updateBuilder.buildSQL()
        );

        assertEquals(
                List.of("Roman", 22, 20),
                updateBuilder.provideValues()
        );
    }

}