package ru.nsu.ccfit.orm.core.sql.query.builder;

import java.sql.Array;
import java.sql.Connection;
import ru.nsu.ccfit.orm.core.sql.query.common.SQLBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.ValuesProvider;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.KeyWord;
import ru.nsu.ccfit.orm.core.sql.query.common.consts.Symbol;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SequencedCollection;
import java.util.stream.Collectors;

public class BuilderUtils {

    public static String enumerateBuildableWithSeparator(
            List<? extends SQLBuilder> buildableElements, String separator
    ) {
        return buildableElements.stream()
                .map(SQLBuilder::buildSQL)
                .collect(Collectors.joining("%s ".formatted(separator)));
    }

    public static String enumerateObjectsWithSeparator(List<?> elements, String separator) {
        return elements.stream()
                .map(Object::toString)
                .collect(Collectors.joining("%s ".formatted(separator)));
    }

    public static String enumerateVariablesWithSeparator(int size, String separator) {
        return enumerateObjectsWithSeparator(Collections.nCopies(size, KeyWord.VARIABLE.getKeyword()), separator);
    }

    public static List<?> extractAndLinkValues(SequencedCollection<?> valuesProviders) {
        return valuesProviders.stream()
                .filter(h -> h instanceof ValuesProvider)
                .map(h -> (ValuesProvider) h)
                .flatMap(h -> h.provideValues().stream())
                .toList();
    }

    public static String complexBuildSQL(SequencedCollection<SQLBuilder> sqlBuilders) {
        return sqlBuilders.stream()
                .filter(SQLBuilder::shouldCreateSQL)
                .map(SQLBuilder::buildSQL)
                .collect(Collectors.joining(Symbol.SPACE.getSymbol()));
    }

    public static String clearExtraSpaces(String dirtyString) {
        return dirtyString.replaceAll("\\s+", " ");
    }

    public static void fillPreparedStatement(PreparedStatement preparedStatement, List<?> params, Connection connection)
            throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            int parameterIndex = i + 1;

            switch (value) {
                case Date date -> preparedStatement.setDate(parameterIndex, new java.sql.Date(date.getTime()));
                case List<?> list -> {
                    Array array = connection.createArrayOf("BIGINT", list.toArray());
                    preparedStatement.setArray(parameterIndex, array);
                }
                case Object object -> preparedStatement.setObject(parameterIndex, object);
            }
        }
    }

}
