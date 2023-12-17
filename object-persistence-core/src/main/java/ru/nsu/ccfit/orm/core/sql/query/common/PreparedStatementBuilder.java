package ru.nsu.ccfit.orm.core.sql.query.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementBuilder {

    PreparedStatement buildPreparedStatement(Connection connection) throws SQLException;

}
