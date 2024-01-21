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
            .where(
                ConditionFactory.or(
                    ConditionFactory.equals("{u}id", 21),
                    ConditionFactory.or(
                        ConditionFactory.equals("{u}id", 22),
                        ConditionFactory.equals("{u}id", 23)
                    )
                )
            );
        
        assertEquals(
            "DELETE FROM users u " +
                "WHERE (u.id = ? OR (u.id = ? OR u.id = ?))",
            deleteBuilder.buildSQL()
        );
        
        assertEquals(
            List.of(21, 22, 23),
            deleteBuilder.provideValues()
        );
    }
    
}