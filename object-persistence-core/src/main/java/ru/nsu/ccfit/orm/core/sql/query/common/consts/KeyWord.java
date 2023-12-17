package ru.nsu.ccfit.orm.core.sql.query.common.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KeyWord {
    ASC("ASC"),
    DESC("DESC"),
    VARIABLE("?"),
    ALL("*");

    private String keyword;

    @Override
    public String toString() {
        return keyword;
    }
}
