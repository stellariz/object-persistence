package ru.nsu.ccfit.orm.core.sql.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.meta.EntityMetaDataManager;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;
import ru.nsu.ccfit.orm.model.utils.FieldInfo;
import ru.nsu.ccfit.orm.model.utils.IdRowData;

public class SqlConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlConverter.class);
    private final EntityMetaDataManager entityMetaDataManager;

    public SqlConverter(EntityMetaDataManager entityMetaDataManager) {
        this.entityMetaDataManager = entityMetaDataManager;
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
            return resultSetToObject(rs, clazz, (T)clazz.getDeclaredConstructor().newInstance());
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
            if (columnsList.contains(idRowData.idFieldName().toLowerCase())) {
                idRowData.fieldInfo().setter().invoke(instance, rs.getObject(idRowData.idFieldName().toLowerCase()));
            }
            for (String baseRow : tableMetaData.rowsData().keySet()) {
                FieldInfo fieldInfo = tableMetaData.rowsData().get(baseRow);
                if (columnsList.contains(baseRow.toLowerCase())) {
                    fieldInfo.setter().invoke(instance, rs.getObject(baseRow.toLowerCase()));
                }
            }
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
}
