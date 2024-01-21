package ru.nsu.ccfit.orm.core.meta.manager;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.orm.core.sql.query.builder.SelectBuilder;
import ru.nsu.ccfit.orm.core.sql.utils.SqlConverter;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public class DefaultEntitySearchProvider implements EntitySearchProvider {

    private final DataSource dataSource;
    private final SqlConverter sqlConverter;

    @Override
    public <T> List<T> searchByQuery(SelectBuilder selectBuilder, TableMetaData tableMetaData, Class<T> objectClass) {
        try (var selectStatement = selectBuilder.buildPreparedStatement(dataSource.getConnection())) {
            return sqlConverter.extractEntitiesFromExecutedStatement(selectStatement, objectClass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
