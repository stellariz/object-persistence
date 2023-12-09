package ru.nsu.ccfit.orm.core.meta;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.sql.query.QueryBuilder;
import ru.nsu.ccfit.orm.core.sql.query.QueryUtils;
import ru.nsu.ccfit.orm.core.sql.utils.SqlConverter;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.nsu.ccfit.orm.core.utils.FieldUtilsManager.getFieldValue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public class DefaultEntityManager<T> implements EntityManager<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEntityManager.class);

    private final DataSource dataSource;
    private final EntityMetaDataManager entityMetaDataManager;
    private final SqlConverter sqlConverter;
    private final QueryBuilder queryBuilder;

    @Override
    public T findById(Class<?> objectClass, Object key) {
        TableMetaData tableMetaData = getTableMetaDataByObject(objectClass);

        // TODO: 10.12.2023 (r.yatmanov): Переделать на sql query builder (абстрактная фабрика?)
        String sqlQuery = QueryUtils.buildFindByIdQuery(tableMetaData);

        List<T> searchResult = findAll(objectClass, sqlQuery, List.of(key));
        if (searchResult.isEmpty()) {
            LOGGER.info("There is no entity of %s with id %s".formatted(objectClass.getName(), key));
        } else if (searchResult.size() != 1) {
            throw new IllegalArgumentException(
                    "Found two entities %s with id %s".formatted(objectClass.getName(), key)
            );
        }
        return searchResult.getFirst();
    }

    @Override
    public List<T> findAll(Class<?> objectClass) {
        TableMetaData tableMetaData = getTableMetaDataByObject(objectClass);

        // TODO: 10.12.2023 (r.yatmanov): Переделать на sql query builder (абстрактная фабрика?)
        String sqlQuery = QueryUtils.buildFindAllSimpleQuery(tableMetaData);

        return findAll(objectClass, sqlQuery, Collections.emptyList());
    }

    @Override
    public T create(T object) {
        TableMetaData tableMetaData = getTableMetaDataByObject(object.getClass());

        // TODO: 05.12.2023 (r.popov): добавить логику ManyToOne, OneToMany
        //  (сейчас всё линейно, работаем с примитивами и их оболочками)
        Set<String> parameters = tableMetaData.rowsData().keySet();
        List<Object> values = tableMetaData.rowsData().values().stream()
                .map(value -> getFieldValue(value, object))
                .collect(Collectors.toList());

        // TODO: 10.12.2023 (r.yatmanov): Переделать на sql query builder (абстрактная фабрика?)
        String sqlQuery = QueryUtils.buildInsertQuery(tableMetaData, parameters);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            sqlConverter.fillPreparedStatement(preparedStatement, values);

            LOGGER.debug("Executing sql query: \"{}\"", sqlQuery);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return findById(object.getClass(), generatedKeys.getObject(1));
                } else {
                    throw new IllegalArgumentException("Nothing to create");
                }
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public T update(T object) {
        TableMetaData tableMetaData = getTableMetaDataByObject(object.getClass());

        Set<String> parameters = tableMetaData.rowsData().keySet();
        List<Object> values = tableMetaData.rowsData().values().stream()
                .map(value -> getFieldValue(value, object))
                .collect(Collectors.toCollection(ArrayList::new));

        // TODO: 10.12.2023 (r.yatmanov): Переделать на sql query builder (абстрактная фабрика?)
        String sqlQuery = QueryUtils.buildUpdateQuery(tableMetaData, parameters);

        // TODO: 05.12.2023 (r.popov): добавить логику ManyToOne, OneToMany
        if (tableMetaData.idRowData() != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                values.add(tableMetaData.idRowData().fieldInfo().getter().invoke(object));

                sqlConverter.fillPreparedStatement(preparedStatement, values);
                LOGGER.debug("Executing sql query: \"{}\"", sqlQuery);
                preparedStatement.executeUpdate();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return findById(object.getClass(), generatedKeys.getObject(1));
                    } else {
                        throw new IllegalArgumentException("Nothing to create");
                    }
                }

            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }

    @Override
    public boolean delete(T object) {
        TableMetaData tableMetaData = getTableMetaDataByObject(object.getClass());

        // TODO: 10.12.2023 (r.yatmanov): Переделать на sql query builder (абстрактная фабрика?)
        String sqlQuery = QueryUtils.buildDeleteQuery(tableMetaData);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            if (tableMetaData.idRowData() != null) {
                var id = tableMetaData.idRowData().fieldInfo().getter().invoke(object);
                sqlConverter.fillPreparedStatement(preparedStatement, List.of(id));
                LOGGER.debug("Executing sql query: \"{}\"", sqlQuery);
                if (preparedStatement.executeUpdate() != 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error during delete object: %s".formatted(object.toString()));
            throw new IllegalArgumentException(e);
        }

        return false;
    }

    private List<T> findAll(Class<?> clazz, String sqlQuery, List<?> params) {
        List<T> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            sqlConverter.fillPreparedStatement(preparedStatement, params);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(sqlConverter.resultSetToObject(resultSet, clazz));
                }
            }
            LOGGER.debug("Executing sql query: \"{}\"", sqlQuery);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
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

    private TableMetaData getTableMetaDataByObject(Class<?> clazz) {
        return entityMetaDataManager.getMetaData(clazz)
                .orElseThrow(() -> new IllegalArgumentException(
                        "There is no entity with class : %s".formatted(clazz.getName()))
                );
    }
}
