package ru.nsu.ccfit.orm.core.sql.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.postgresql.ds.PGSimpleDataSource;
import ru.nsu.ccfit.orm.core.sql.utils.SqlConverter;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

public class QuerySave {
    private static final PGSimpleDataSource pgTestDataSource;

    static {
        pgTestDataSource = new PGSimpleDataSource();
        pgTestDataSource.setURL("jdbc:postgresql://localhost:5432/postgres");
        pgTestDataSource.setDatabaseName("postgres");
        pgTestDataSource.setUser("postgres");
        pgTestDataSource.setPassword("password");
    }

    private QuerySave() {
    }

    public static long insert(List<Object> values, TableMetaData tableMetaData) {
        String op = "?,";
        long id = 0;
        // do not create table = error + do not create columns = error
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableMetaData.tableName())
                .append(" (")
                .append(tableMetaData.concatenatedFieldNames())
                .append(") VALUES (")
                .append(op.repeat(tableMetaData.rowsData().size()));
        query.deleteCharAt(query.length() - 1);
        query.append(")");
        try (Connection connection = pgTestDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString(),
                     Statement.RETURN_GENERATED_KEYS)) {
            SqlConverter.fillPreparedStatement(preparedStatement, values);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error while inserting object", e);
        }
        return id;
    }
}
