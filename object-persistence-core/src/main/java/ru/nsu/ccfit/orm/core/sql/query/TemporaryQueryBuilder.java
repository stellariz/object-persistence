package ru.nsu.ccfit.orm.core.sql.query;

import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;

import java.util.Map;

/**
 * Временный класс, который использует строитель запросов SQL для создания таблицы.
 * Используется: AdvancedSQL
 */
public class TemporaryQueryBuilder implements QueryBuilder {

    /**
     * Очень упрощенный конструктор на создание запроса заблицы БД
     **/
    @Override
    public String buildSqlCreateTableQuery(TableMetaData tableMetaData) {
        var idField = tableMetaData.idRowData().fieldInfo();
        return new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS %s ".formatted(tableMetaData.tableName()))
                .append("(%s)".formatted(collectColumns(tableMetaData.allRowsData(), idField)))
                .toString();
    }

    private String collectColumns(Map<String, FieldInfo> rowsData, FieldInfo idField) {
        return rowsData.entrySet().stream()
                .map(entry -> getColumn(entry.getValue(), entry.getKey(), entry.getValue().equals(idField)))
                .collect(Collectors.joining(", "));
    }

    private String getColumn(FieldInfo fieldInfo, String fieldName, boolean isPrimaryKey) {
        var columnType = switch (fieldInfo.field().getType().getSimpleName()) {
            case "String" -> "VARCHAR(255)";
            case "int", "Integer" -> "INTEGER";
            case "double", "Double", "float", "Float" -> "DOUBLE PRECISION";
            case "BigDecimal" -> "NUMERIC";
            case "long", "Long", "BigInteger" -> "BIGINT";
            case "boolean", "Boolean", "bit" -> "BIT";
            case "Date" -> "DATE";
            case "List" -> "bigint[]";
            default -> "BIGINT";
        };

        return "%s %s%s".formatted(fieldName, columnType, isPrimaryKey ? " PRIMARY KEY" : StringUtils.EMPTY);
    }

}
