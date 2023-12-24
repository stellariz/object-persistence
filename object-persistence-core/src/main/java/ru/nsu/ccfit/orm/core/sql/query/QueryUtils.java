package ru.nsu.ccfit.orm.core.sql.query;

import java.util.stream.Collectors;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.util.Collection;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.chop;
import static org.apache.commons.lang3.StringUtils.repeat;

// todo: 10.12.2023 r.yatmanov Добавить фабрику запросов, использовать в нём собственный построитель запросов, выпилить лишние либы
public class QueryUtils {

    public static String buildFindByIdQuery(TableMetaData tableMetaData) {
        var tableName = tableMetaData.tableName();
        var tableShortName = tableMetaData.tableName().charAt(0);
        var idName = tableMetaData.idRowData().idFieldName();
        return new StringBuilder()
                .append("SELECT * ")
                .append("FROM %s %s ".formatted(tableName, tableShortName))
                .append("WHERE %s.%s = ?".formatted(tableShortName, idName))
                .toString();
    }

    public static String buildFindAllSimpleQuery(TableMetaData tableMetaData) {
        var tableName = tableMetaData.tableName();
        var tableShortName = tableMetaData.tableName().charAt(0);
        return new StringBuilder()
                .append("SELECT * ")
                .append("FROM %s %s".formatted(tableName, tableShortName))
                .toString();
    }

    public static String buildDeleteQuery(TableMetaData tableMetaData) {
        var tableName = tableMetaData.tableName();
        var tableShortName = tableMetaData.tableName().charAt(0);
        var idName = tableMetaData.idRowData().idFieldName();
        return new StringBuilder()
                .append("DELETE ")
                .append("FROM %s %s ".formatted(tableName, tableShortName))
                .append("WHERE %s.%s = ?".formatted(tableShortName, idName))
                .toString();
    }

    public static String buildInsertQuery(TableMetaData tableMetaData, Set<String> parameters) {
        var tableName = tableMetaData.tableName();
        return new StringBuilder()
                .append("INSERT INTO %s (%s) ".formatted(tableName, toStringInsertTuple(parameters)))
                .append("VALUES (%s) ".formatted(chop(repeat("?,", parameters.size()))))
                .append("RETURNING %s".formatted(tableMetaData.idRowData().idFieldName()))
                .toString();
    }

    public static String buildUpdateQuery(TableMetaData tableMetaData, Set<String> parameters) {
        var tableName = tableMetaData.tableName();
        var idName = tableMetaData.idRowData().idFieldName();
        return new StringBuilder()
                .append("UPDATE %s ".formatted(tableName))
                .append("SET %s ".formatted(toStringUpdateTuple(parameters)))
                .append("WHERE %s = ? ".formatted(idName))
                .append("RETURNING %s".formatted(tableMetaData.idRowData().idFieldName()))
                .toString();
    }

    private static String toStringInsertTuple(Collection<String> collection) {
        return collection.stream()
                .collect(Collectors.joining(", "));
    }

    private static String toStringUpdateTuple(Collection<String> collection) {
        return collection.stream()
                .map("%s = ?"::formatted)
                .collect(Collectors.joining(", "));
    }

}
