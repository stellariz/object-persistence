package ru.nsu.ccfit.orm.core.meta.manager;

import com.google.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.dao.EntityOperationsProvider;
import ru.nsu.ccfit.orm.core.repository.dsl.selector.EntitySelector;
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
    private final EntitySearchProvider entitySearchProvider;

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
        if (object == null) {
            return null;
        }

        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(object.getClass());

        T existedObject = getExistedObject(object, tableMetaData.idRowData().fieldInfo().getter());
        if (existedObject != null) {
            return existedObject;
        }

        iterativeApplyFunctionToComplexRows(tableMetaData, object, this::create);

        return entityOperationsProvider.create(tableMetaData, object);
    }

    @Override
    public <T> boolean delete(T object) {
        if (object == null) {
            return true;
        }
        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(object.getClass());

        iterativeApplyFunctionToComplexRows(tableMetaData, object, this::delete);

        return entityOperationsProvider.delete(tableMetaData, object);
    }

    @Override
    public <T> T update(T object) {
        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(object.getClass());
        // TODO: check object's fields that were updated
        return entityOperationsProvider.update(tableMetaData, object);
    }

    @Override
    public <T> EntitySelector<? extends T> customSearch(Class<? extends T> objectClass) {
        TableMetaData tableMetaData = entityMetaDataManager.unsafeGetMetaData(objectClass);
        return new EntitySelector<>(tableMetaData, entitySearchProvider, objectClass);
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

    private <T> T getExistedObject(T object, Method idGetter) {

        try {
            T findObject = (T) findById(object.getClass(), idGetter.invoke(object));
            if (findObject != null) {
                return findObject;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
        }
        return null;
    }

    private <T> void iterativeApplyFunctionToComplexRows(TableMetaData tableMetaData,
                                                         T object, Consumer<? super Object> action) {
        Map<TableMetaData, Object> oneToOneObjects = valuesCollector.collectOneToOneValues(tableMetaData, object);
        Map<TableMetaData, List<Object>> oneToManyObjects = valuesCollector.collectOneToManyValues(tableMetaData, object);
        // TODO: for many-to-one relationship action not always should be applied
        Map<TableMetaData, Object> manyToOneObjects = valuesCollector.collectManyToOneValues(tableMetaData, object);

        oneToOneObjects.values().forEach(action);

        oneToManyObjects.values().stream().flatMap(List::stream).forEach(action);

        manyToOneObjects.values().forEach(action);
    }
}
