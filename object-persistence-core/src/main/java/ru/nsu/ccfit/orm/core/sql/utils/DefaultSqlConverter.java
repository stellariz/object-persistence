package ru.nsu.ccfit.orm.core.sql.utils;

import com.google.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.meta.manager.EntityManager;
import ru.nsu.ccfit.orm.core.meta.manager.EntityMetaDataManager;
import ru.nsu.ccfit.orm.core.sql.query.builder.DeleteBuilder;
import ru.nsu.ccfit.orm.core.sql.query.builder.InsertBuilder;
import ru.nsu.ccfit.orm.core.sql.query.builder.SelectBuilder;
import ru.nsu.ccfit.orm.core.sql.query.builder.UpdateBuilder;
import ru.nsu.ccfit.orm.core.sql.query.common.element.condtion.ConditionFactory;
import ru.nsu.ccfit.orm.core.sql.utils.fillers.DefaultTypesFiller;
import ru.nsu.ccfit.orm.core.sql.utils.fillers.IdDataFiller;
import ru.nsu.ccfit.orm.core.sql.utils.fillers.ManyToOneRelationshipFiller;
import ru.nsu.ccfit.orm.core.sql.utils.fillers.ObjectFiller;
import ru.nsu.ccfit.orm.core.sql.utils.fillers.OneToManyRelationshipFiller;
import ru.nsu.ccfit.orm.core.sql.utils.fillers.OneToOneRelationshipFiller;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

public class DefaultSqlConverter implements SqlConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSqlConverter.class);
    private final EntityMetaDataManager entityMetaDataManager;
    private final List<ObjectFiller> objectFillers;

    @Inject
    private DefaultSqlConverter(EntityMetaDataManager entityMetaDataManager, EntityManager entityManager) {
        this.entityMetaDataManager = entityMetaDataManager;
        this.objectFillers = createFillers(entityManager);
    }

    private List<ObjectFiller> createFillers(EntityManager entityManager) {
        return new LinkedList<>(List.of(
                new IdDataFiller(),
                new DefaultTypesFiller(),
                new OneToOneRelationshipFiller(entityManager),
                new OneToManyRelationshipFiller(entityManager),
                new ManyToOneRelationshipFiller(entityManager)
        ));
    }

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

    public <T> List<T> extractEntitiesFromExecutedStatement(PreparedStatement returnableEntitiesStatement, Class<?> objectClass) {
        List<T> result = new ArrayList<>();
        try (ResultSet resultSet = returnableEntitiesStatement.executeQuery()) {
            while (resultSet.next()) {
                result.add(resultSetToObject(resultSet, objectClass));
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }

    private <T> T resultSetToObject(ResultSet rs, Class<?> clazz) {
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
        List<String> columnsList = getColumnsList(rs);

        try {
            for (var filler : objectFillers) {
                filler.fillObject(tableMetaData, instance, columnsList, rs);
            }
        } catch (Exception e) {
            LOGGER.error("Error during filling the instance of clazz : %s".formatted(clazz.getName()));
            throw new IllegalArgumentException(e);
        }
        return instance;
    }

    private List<String> getColumnsList(ResultSet rs) {
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
}
