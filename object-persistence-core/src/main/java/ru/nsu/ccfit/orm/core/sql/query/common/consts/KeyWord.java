package ru.nsu.ccfit.orm.core.sql.query.common.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KeyWord {
    SELECT("SELECT"),
    INSERT_INTO("INSERT INTO"),
    AS("AS"),
    FROM("FROM"),
    WHERE("WHERE"),
    ON("ON"),
    LIMIT("LIMIT"),
    GROUP_BY("GROUP BY"),
    ORDER_BY("ORDER BY"),
    DISTINCT("DISTINCT"),
    UNION("UNION"),
    UNION_ALL("UNION ALL"),
    ASC("ASC"),
    DESC("DESC"),
    VARIABLE("?"),
    VALUES("VALUES"),
    RETURNING("RETURNING"),
    DELETE("DELETE"),
    ALL("*");

    private String keyword;

    @Override
    public String toString() {
        return keyword;
    }
}
