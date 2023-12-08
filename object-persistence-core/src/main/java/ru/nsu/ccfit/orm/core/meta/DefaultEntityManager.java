package ru.nsu.ccfit.orm.core.meta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.sql.utils.SqlConverter;
import ru.nsu.ccfit.orm.core.utils.FieldUtilsManager;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

public class DefaultEntityManager<T> implements EntityManager<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEntityManager.class);
    // TODO: 05.12.2023 (r.popov): replace to DAO
    private DataSource dataSource;
    private final EntityMetaDataManager<T> entityMetaDataManager = new DefaultEntityMetaDataManager<>();
    private final SqlConverter sqlConverter = new SqlConverter(entityMetaDataManager);


    @Override
    public T findById(Class<T> objectClass, Object key) {
        Optional<TableMetaData> metaData = entityMetaDataManager.getMetaData(objectClass);
        TableMetaData tableMetaData =
                metaData.orElseThrow(() -> new IllegalArgumentException(
                        "There is no entity with class : %s".formatted(objectClass.getName()))
                );
        // TODO: 05.12.2023 (r.popov): использовать sql query builder (абстрактная фабрика?)
        String sqlQuery = null;
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
    public List<T> findAll(Class<T> objectClass) {
        // TODO: 05.12.2023 (r.popov): использовать sql query builder
        String sqlQuery = null;
        return findAll(objectClass, sqlQuery, Collections.emptyList());
    }

    @Override
    public T create(T object) {
        Optional<TableMetaData> metaData = entityMetaDataManager.getMetaData(object.getClass());
        TableMetaData tableMetaData =
                metaData.orElseThrow(() -> new IllegalArgumentException(
                        "There is no entity with class : %s".formatted(object.getClass().getName()))
                );

        List<Object> values = new ArrayList<>();

        // TODO: 05.12.2023 (r.popov): добавить логику ManyToOne, OneToMany
        for (var entry : tableMetaData.rowsData().entrySet()) {
            values.add(FieldUtilsManager.getFieldValue(entry.getValue(), object));
        }


        // TODO: 05.12.2023 (r.popov): использовать sql query builder
        String sqlQuery = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {

            sqlConverter.fillPreparedStatement(preparedStatement, values);
            preparedStatement.executeUpdate();
            LOGGER.debug("Executing sql query: \"{}\"", sqlQuery);

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return findById((Class<T>) object.getClass(), generatedKeys.getObject("scope_identity()"));
                } else {
                    throw new IllegalArgumentException("Nothing to create");
                }
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean update(T object) {
        Optional<TableMetaData> metaData = entityMetaDataManager.getMetaData(object.getClass());
        TableMetaData tableMetaData =
                metaData.orElseThrow(() -> new IllegalArgumentException(
                        "There is no entity with class : %s".formatted(object.getClass().getName()))
                );

        List<Object> values = new ArrayList<>();

        for (var entry : tableMetaData.rowsData().entrySet()) {
            values.add(FieldUtilsManager.getFieldValue(entry.getValue(), object));
        }

        // TODO: 05.12.2023 (r.popov): использовать sql query builder
        String sqlQuery = null;
        // TODO: 05.12.2023 (r.popov): добавить логику ManyToOne, OneToMany
        if (tableMetaData.idRowData() != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                sqlConverter.fillPreparedStatement(preparedStatement, values);
                if (preparedStatement.executeUpdate() != 0) {
                    LOGGER.debug("Executing sql query: \"{}\"", sqlQuery);
                    return true;
                }
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return false;
    }

    @Override
    public boolean delete(T object) {
        Optional<TableMetaData> tableInfo = entityMetaDataManager.getMetaData(object.getClass());
        TableMetaData tableMetaData =
                tableInfo.orElseThrow(() -> new IllegalArgumentException(
                        "There is no entity with class : %s".formatted(object.getClass().getName()))
                );
        // TODO: 05.12.2023 (r.popov): использовать sql query builder
        String sqlQuery = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            if (tableMetaData.idRowData() != null) {
                preparedStatement.setObject(1, tableMetaData.idRowData().fieldInfo().getter().invoke(object));
                if (preparedStatement.executeUpdate() != 0) {
                    LOGGER.debug("Executing sql query: \"{}\"", sqlQuery);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error during delete object: %s".formatted(object.toString()));
            throw new IllegalArgumentException(e);
        }

        return false;
    }

    private List<T> findAll(Class<T> clazz, String sqlQuery, List<Object> params) {
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
}
