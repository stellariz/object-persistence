package ru.nsu.ccfit.orm.core.sql.query.common.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Symbol {
    COMMA(","),
    SPACE(" "),
    UNDERLINE("_"),
    PARENTHESES_OPEN("("),
    PARENTHESES_CLOSE(")"),
    VARIABLE("?");

    private String symbol;

    public static boolean isUnderline(String symbol) {
        return UNDERLINE.getSymbol().equals(symbol);
    }

    @Override
    public String toString() {
        return symbol;
    }
}
