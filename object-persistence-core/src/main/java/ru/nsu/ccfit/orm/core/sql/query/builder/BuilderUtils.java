package ru.nsu.ccfit.orm.core.sql.query.builder;

import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class BuilderUtils {

    public static StringBuilder enumerateBuildableWithSeparator(
            List<? extends SQLBuilder> buildableElements, String separator
    ) {
        StringBuilder enumeration = new StringBuilder();
        for (int i = 0; i < buildableElements.size(); i++) {
            enumeration.append(buildableElements.get(i).buildSQL());
            if (i < buildableElements.size() - 1) {
                enumeration.append(separator).append(Symbol.SPACE);
            }
        }
        return enumeration;
    }

    public static StringBuilder enumerateVariablesWithSeparator(int size, String separator) {
        StringBuilder enumeration = new StringBuilder();
        for (int i = 0; i < size; i++) {
            enumeration.append(KeyWord.VARIABLE);
            if (i < size - 1) {
                enumeration.append(separator).append(Symbol.SPACE);
            }
        }
        return enumeration;
    }

    public static StringBuilder enumerateObjectsWithSeparator(List<?> elements, String separator, boolean withQuotes) {
        StringBuilder enumeration = new StringBuilder();
        String valuePattern = withQuotes ? "'%s'" : "%s";

        for (int i = 0; i < elements.size(); i++) {
            String formattedValue = elements.get(i) instanceof String ?
                    valuePattern.formatted(elements.get(i)) : elements.get(i).toString();
            enumeration.append(formattedValue);
            if (i < elements.size() - 1) {
                enumeration.append(separator).append(Symbol.SPACE);
            }
        }

        return enumeration;
    }

    public static String clearDublicatedSpaces(String dirtyString) {
        return dirtyString.replaceAll("\\s+", " ");
    }

    public static void fillPreparedStatement(PreparedStatement preparedStatement, List<?> params)
            throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            int parameterIndex = i + 1;

            switch (value) {
                case Date date -> preparedStatement.setDate(parameterIndex, new java.sql.Date(date.getTime()));
                case Object object -> preparedStatement.setObject(parameterIndex, object);
            }
        }
    }

}
