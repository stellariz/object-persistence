package ru.nsu.ccfit.orm.core.meta.manager;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.orm.core.meta.ValuesCollector;
import ru.nsu.ccfit.orm.core.sql.utils.SqlConverter;
import ru.nsu.ccfit.orm.model.meta.TableMetaData;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public class BasicEntityOperationsProvider implements EntityOperationsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicEntityOperationsProvider.class);
    private final EntityMetaDataManager entityMetaDataManager;
    private final ValuesCollector valuesCollector;
    private final SqlConverter sqlConverter;
    private final DataSource dataSource;

    @Override
    public <T> T create(TableMetaData tableMetaData, Object object) {
        if (Objects.isNull(object)) {
            return null;
        }

        var columnsWithValues = valuesCollector.collectColumnAndValuesPairs(tableMetaData, object);
        try (var connection = dataSource.getConnection();
             var insertStatement = sqlConverter.prepareInsertStatement(tableMetaData, columnsWithValues, connection)) {
            insertStatement.executeUpdate();
            return getByGeneratedId(insertStatement, object);
        } catch (SQLException e) {
            LOGGER.error("Error during creating object: %s".formatted(object.toString()));
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean delete(TableMetaData tableMetaData, Object object) {
        if (Objects.isNull(object) || Objects.isNull(tableMetaData.idRowData())) {
            return false;
        }

        var columnsWithValues = valuesCollector.collectColumnAndValuesPairs(tableMetaData, object);
        try (var connection = dataSource.getConnection();
             var deleteStatement = sqlConverter.prepareDeleteStatement(tableMetaData, columnsWithValues, connection)) {
            return deleteStatement.executeUpdate() != 0;
        } catch (Exception e) {
            LOGGER.error("Error during delete object: %s".formatted(object.toString()));
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public <T> T update(TableMetaData tableMetaData, Object object) {
        if (Objects.isNull(object) || Objects.isNull(tableMetaData.idRowData())) {
            return null;
        }

        var columnsWithValues = valuesCollector.collectColumnAndValuesPairs(tableMetaData, object);
        try (var connection = dataSource.getConnection();
             var preparedStatement = sqlConverter.prepareUpdateStatement(tableMetaData, columnsWithValues, connection)) {
            preparedStatement.executeUpdate();
            return getByGeneratedId(preparedStatement, object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public <T> T findById(TableMetaData tableMetaData, Object key, Class<?> objectClass) {
        var columnsWithValues = Map.of(tableMetaData.idRowData().idFieldName(), key);

        try (var connection = dataSource.getConnection();
             var selectByIdStatement = sqlConverter.prepareSelectByIdStatement(tableMetaData, columnsWithValues, connection)) {
            List<T> searchResult = getEntities(selectByIdStatement, objectClass);

            if (searchResult.isEmpty()) {
                LOGGER.info("There is no entity of %s with id %s".formatted(objectClass.getName(), key));
            } else if (searchResult.size() != 1) {
                throw new IllegalArgumentException(
                        "Found two entities %s with id %s".formatted(objectClass.getName(), key)
                );
            }

            return searchResult.getFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> findByAll(TableMetaData tableMetaData, Class<?> objectClass) {
        try (var connection = dataSource.getConnection();
             var selectByIdStatement = sqlConverter.prepareSelectAllStatement(tableMetaData, connection)) {
            return getEntities(selectByIdStatement, objectClass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T getByGeneratedId(PreparedStatement returnableIdStatement, Object object) {
        try (ResultSet generatedKeys = returnableIdStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                var tableMetaData = entityMetaDataManager.unsafeGetMetaData(object.getClass());
                return findById(tableMetaData, generatedKeys.getObject(1), object.getClass());
            } else {
                throw new IllegalArgumentException("Nothing to create");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> getEntities(PreparedStatement returnableEntitiesStatement, Class<?> objectClass) {
        List<T> result = new ArrayList<>();
        try (ResultSet resultSet = returnableEntitiesStatement.executeQuery()) {
            while (resultSet.next()) {
                result.add(sqlConverter.resultSetToObject(resultSet, objectClass));
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }

}
