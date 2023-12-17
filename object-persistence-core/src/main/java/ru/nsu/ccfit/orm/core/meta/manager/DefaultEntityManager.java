package ru.nsu.ccfit.orm.core.meta.manager;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.meta.ValuesCollector;
import ru.nsu.ccfit.orm.core.sql.query.QueryBuilder;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public class DefaultEntityManager implements EntityManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEntityManager.class);
    private final DataSource dataSource;
    private final ValuesCollector valuesCollector;
    private final EntityMetaDataManager entityMetaDataManager;
    private final QueryBuilder queryBuilder;
    private final EntityOperationsProvider entityOperationsProvider;

    @Override
    public <T> T findById(Class<? extends T> objectClass, Object key) {
        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(objectClass);
        return entityOperationsProvider.findById(tableMetaData, key, objectClass);
    }

    @Override
    public <T> List<T> findAll(Class<? extends T> objectClass) {
        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(objectClass);
        return entityOperationsProvider.findByAll(tableMetaData, objectClass);
    }

    @Override
    public <T> T create(T object) {
        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(object.getClass());
        // TODO: 05.12.2023 (r.popov): добавить логику ManyToOne, OneToMany
        Map<TableMetaData, Object> oneToOneObjects = valuesCollector.collectOneToOneValues(tableMetaData, object);

        for (var entry : oneToOneObjects.entrySet()) {
            entityOperationsProvider.create(entry.getKey(), entry.getValue());
        }

        return entityOperationsProvider.create(tableMetaData, object);
    }

    @Override
    public <T> boolean delete(T object) {
        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(object.getClass());
        Map<TableMetaData, Object> oneToOneObjects = valuesCollector.collectOneToOneValues(tableMetaData, object);

        for (var entry : oneToOneObjects.entrySet()) {
            entityOperationsProvider.delete(entry.getKey(), entry.getValue());
        }

        return entityOperationsProvider.delete(tableMetaData, object);
    }

    @Override
    public <T> T update(T object) {
        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(object.getClass());
        Map<TableMetaData, Object> oneToOneObjects = valuesCollector.collectOneToOneValues(tableMetaData, object);

        for (var entry : oneToOneObjects.entrySet()) {
            entityOperationsProvider.update(entry.getKey(), entry.getValue());
        }

        return entityOperationsProvider.update(tableMetaData, object);
    }

    @Override
    public void createTableForClass(Class<?> clazz) {
        TableMetaData tableMetaData = entityMetaDataManager.getMetaData(clazz)
                .orElse(entityMetaDataManager.saveMetaData(clazz));

        String createTableQuery = queryBuilder.buildSqlCreateTableQuery(tableMetaData);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(createTableQuery)) {
            LOGGER.debug("Executing sql query: \"{}\"", createTableQuery);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
