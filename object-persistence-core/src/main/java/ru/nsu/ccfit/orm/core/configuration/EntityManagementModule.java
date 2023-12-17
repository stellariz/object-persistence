package ru.nsu.ccfit.orm.core.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.postgresql.ds.PGSimpleDataSource;
import ru.nsu.ccfit.orm.core.meta.BasicValuesCollector;
import ru.nsu.ccfit.orm.core.meta.ValuesCollector;
import ru.nsu.ccfit.orm.core.meta.manager.*;
import ru.nsu.ccfit.orm.core.sql.query.QueryBuilder;
import ru.nsu.ccfit.orm.core.sql.query.TemporaryQueryBuilder;
import ru.nsu.ccfit.orm.core.sql.utils.DefaultSqlConverter;
import ru.nsu.ccfit.orm.core.sql.utils.SqlConverter;

import javax.sql.DataSource;

public class EntityManagementModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EntityMetaDataManager.class).to(DefaultEntityMetaDataManager.class).in(Singleton.class);
        bind(QueryBuilder.class).to(TemporaryQueryBuilder.class).in(Singleton.class);
        bind(SqlConverter.class).to(DefaultSqlConverter.class).in(Singleton.class);
        bind(ValuesCollector.class).to(BasicValuesCollector.class).in(Singleton.class);
        bind(EntityOperationsProvider.class).to(BasicEntityOperationsProvider.class).in(Singleton.class);
        bind(EntityManager.class).to(DefaultEntityManager.class).in(Singleton.class);
        bind(DataSource.class).toInstance(pgDataSource());
    }

    private DataSource pgDataSource() {
        var dataSource = new PGSimpleDataSource();

        dataSource.setURL("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setDatabaseName("postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("password");

        return dataSource;
    }

}
