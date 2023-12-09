package ru.nsu.ccfit.orm.core.sql.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SqlConverter {

    void fillPreparedStatement(PreparedStatement preparedStatement, List<?> params) throws SQLException;

    <T> T resultSetToObject(ResultSet rs, Class<?> clazz);

}
