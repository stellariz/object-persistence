package ru.nsu.ccfit.orm.core.sql.query.builder;

import org.junit.jupiter.api.Test;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeleteBuilderTest {

    @Test
    public void testBasicDelete(
    ) {
        DeleteBuilder deleteBuilder = DeleteBuilder.createDelete();

        deleteBuilder
                .from("users u")
                .where(ConditionFactory.equals("{u}name", "DELETABLE"));

        assertEquals(
                "DELETE FROM users u WHERE u.name = ?",
                deleteBuilder.buildSQL()
        );

        assertEquals(
                List.of("DELETABLE"),
                deleteBuilder.provideValues()
        );
    }

}