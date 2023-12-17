package ru.nsu.ccfit.orm.core.sql.utils;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.meta.manager.EntityManager;
import ru.nsu.ccfit.orm.core.meta.manager.EntityMetaDataManager;
import ru.nsu.ccfit.orm.core.sql.query.builder.DeleteBuilder;
import ru.nsu.ccfit.orm.core.sql.query.builder.InsertBuilder;
import ru.nsu.ccfit.orm.core.sql.query.builder.SelectBuilder;
import ru.nsu.ccfit.orm.core.sql.query.builder.UpdateBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionFactory;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Date;
import java.util.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public class DefaultSqlConverter implements SqlConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSqlConverter.class);
    private final EntityMetaDataManager entityMetaDataManager;
    private final EntityManager entityManager;

    @Override
    public PreparedStatement prepareUpdateStatement(
            TableMetaData tableMetaData, Map<String, Object> columnsWithValues, Connection connection
    ) throws SQLException {
        var tableAlias = tableMetaData.tableName().charAt(0);
        var tableNameEncoding = "%s %s".formatted(tableMetaData.tableName(), tableAlias);

        var idColumnName = tableMetaData.idRowData().idFieldName();
        var idColumnValue = columnsWithValues.get(idColumnName);
        var idColumnEncoding = "{%s}%s".formatted(tableAlias, idColumnName);

        var updateSet = new HashMap<>(columnsWithValues);
        updateSet.remove(idColumnName);

        return UpdateBuilder.createUpdate()
                .table(tableNameEncoding)
                .updateSet(updateSet)
                .where(ConditionFactory.equals(idColumnEncoding, idColumnValue))
                .returning(tableMetaData.idRowData().idFieldName())
                .buildPreparedStatement(connection);
    }

    @Override
    public PreparedStatement prepareInsertStatement(
            TableMetaData tableMetaData, Map<String, Object> columnsWithValues, Connection connection
    ) throws SQLException {
        return InsertBuilder.createInsert()
                .table(tableMetaData.tableName())
                .insertSet(columnsWithValues)
                .returning(tableMetaData.idRowData().idFieldName())
                .buildPreparedStatement(connection);
    }

    @Override
    public PreparedStatement prepareDeleteStatement(
            TableMetaData tableMetaData, Map<String, Object> columnsWithValues, Connection connection
    ) throws SQLException {
        var tableAlias = tableMetaData.tableName().charAt(0);
        var tableName = tableMetaData.tableName();
        var idColumnName = tableMetaData.idRowData().idFieldName();
        var tableNameEncoding = "%s %s".formatted(tableName, tableAlias);
        var idColumnEncoding = "{%s}%s".formatted(tableAlias, idColumnName);
        return DeleteBuilder.createDelete()
                .from(tableNameEncoding)
                .where(ConditionFactory.equals(idColumnEncoding, columnsWithValues.get(idColumnName)))
                .buildPreparedStatement(connection);
    }

    @Override
    public PreparedStatement prepareSelectByIdStatement(
            TableMetaData tableMetaData, Map<String, Object> columnsWithValues, Connection connection
    ) throws SQLException {
        var tableAlias = tableMetaData.tableName().charAt(0);
        var idColumnName = tableMetaData.idRowData().idFieldName();
        var tableNameEncoding = "%s %s".formatted(tableMetaData.tableName(), tableAlias);
        var idColumnEncoding = "{%s}%s".formatted(tableAlias, idColumnName);
        return SelectBuilder.createSelect()
                .selectAll()
                .from(tableNameEncoding)
                .where(ConditionFactory.equals(idColumnEncoding, columnsWithValues.get(idColumnName)))
                .buildPreparedStatement(connection);
    }

    @Override
    public PreparedStatement prepareSelectAllStatement(TableMetaData tableMetaData, Connection connection)
            throws SQLException {
        var tableNameEncoding = "%s %s".formatted(tableMetaData.tableName(), tableMetaData.tableName().charAt(0));
        return SelectBuilder.createSelect()
                .selectAll()
                .from(tableNameEncoding)
                .buildPreparedStatement(connection);
    }

    public void fillPreparedStatement(PreparedStatement preparedStatement, List<?> params)
            throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            int parameterIndex = i + 1;
            if (value instanceof Date date) {
                preparedStatement.setDate(parameterIndex, new java.sql.Date(date.getTime()));
            } else {
                preparedStatement.setObject(parameterIndex, value);
            }
        }
    }

    public <T> T resultSetToObject(ResultSet rs, Class<?> clazz) {
        try {
            return resultSetToObject(rs, clazz, (T) clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            LOGGER.error("Error during initialization new instance of clazz : %s".formatted(clazz.getName()));
            throw new IllegalArgumentException(e);
        }
    }

    private <T> T resultSetToObject(ResultSet rs, Class<?> clazz, T instance) {
        Optional<TableMetaData> metaData = entityMetaDataManager.getMetaData(clazz);
        TableMetaData tableMetaData = metaData
                .orElseThrow(() -> new IllegalArgumentException(
                        "There is no metadata for clazz : %s".formatted(clazz.getName()))
                );
        List<String> columnsList = getColumList(rs);
        IdRowData idRowData = tableMetaData.idRowData();

        // TODO: 05.12.2023 (r.popov): добавить логику OneToMany, ManyToOne
        try {
            fillId(idRowData, instance, columnsList, rs);
            fillDefaultTypes(tableMetaData, instance, columnsList, rs);
            fillOneToOneRelationship(tableMetaData, instance, columnsList, rs);
        } catch (Exception e) {
            LOGGER.error("Error during filling the instance of clazz : %s".formatted(clazz.getName()));
            throw new IllegalArgumentException(e);
        }
        return instance;
    }

    private List<String> getColumList(ResultSet rs) {
        List<String> columnsList = new ArrayList<>();
        try {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columns = rsMetaData.getColumnCount();
            for (int x = 1; x <= columns; x++) {
                columnsList.add(rsMetaData.getColumnName(x).toLowerCase());
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return columnsList;
    }

    private void fillId(IdRowData idRowData, Object instance, List<String> columnsList, ResultSet rs)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        if (columnsList.contains(idRowData.idFieldName().toLowerCase())) {
            idRowData.fieldInfo().setter().invoke(instance, rs.getObject(idRowData.idFieldName().toLowerCase()));
        }
    }

    private void fillDefaultTypes(TableMetaData tableMetaData, Object instance, List<String> columnsList, ResultSet rs)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        for (String baseRow : tableMetaData.simpleRowsData().keySet()) {
            FieldInfo fieldInfo = tableMetaData.simpleRowsData().get(baseRow);
            if (columnsList.contains(baseRow.toLowerCase())) {
                fieldInfo.setter().invoke(instance, rs.getObject(baseRow.toLowerCase()));
            }
        }
    }

    private void fillOneToOneRelationship(TableMetaData tableMetaData, Object instance, List<String> columnsList, ResultSet rs)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        for (String complexRow : tableMetaData.oneToOneRowsData().keySet()) {
            FieldInfo fieldInfo = tableMetaData.oneToOneRowsData().get(complexRow);
            if (columnsList.contains(complexRow.toLowerCase()) && rs.getObject(complexRow.toLowerCase()) != null) {
                fieldInfo.setter().invoke(instance, entityManager.findById(fieldInfo.getter().getReturnType(),
                        rs.getObject(complexRow.toLowerCase())));
            }
        }
    }
}
