package ru.nsu.ccfit.orm.core.sql.utils;

import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SqlConverter {

    PreparedStatement prepareUpdateStatement(TableMetaData tableMetaData, Map<String, Object> columnsWithValues, Connection connection) throws SQLException;

    PreparedStatement prepareInsertStatement(TableMetaData tableMetaData, Map<String, Object> columnsWithValues, Connection connection) throws SQLException;

    PreparedStatement prepareDeleteStatement(TableMetaData tableMetaData, Map<String, Object> columnsWithValues, Connection connection) throws SQLException;

    PreparedStatement prepareSelectByIdStatement(TableMetaData tableMetaData, Map<String, Object> columnsWithValues, Connection connection) throws SQLException;

    PreparedStatement prepareSelectAllStatement(TableMetaData tableMetaData, Connection connection) throws SQLException;

    void fillPreparedStatement(PreparedStatement preparedStatement, List<?> params) throws SQLException;

    <T> T resultSetToObject(ResultSet rs, Class<?> clazz);

}
