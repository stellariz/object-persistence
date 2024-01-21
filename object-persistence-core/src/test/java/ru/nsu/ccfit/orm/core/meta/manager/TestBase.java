package ru.nsu.ccfit.orm.core.meta.manager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import javax.sql.DataSource;
import org.junit.Before;
import ru.nsu.ccfit.orm.core.dao.DefaultEntityOperationsProvider;
import ru.nsu.ccfit.orm.core.dao.EntityOperationsProvider;
import ru.nsu.ccfit.orm.core.sql.query.QueryBuilder;
import ru.nsu.ccfit.orm.core.sql.query.TemporaryQueryBuilder;
import ru.nsu.ccfit.orm.core.sql.utils.DefaultSqlConverter;
import ru.nsu.ccfit.orm.core.sql.utils.SqlConverter;

public abstract class TestBase {
    protected Injector injector = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(EntityMetaDataManager.class).to(DefaultEntityMetaDataManager.class).in(Singleton.class);
            bind(QueryBuilder.class).to(TemporaryQueryBuilder.class).in(Singleton.class);
            bind(SqlConverter.class).to(DefaultSqlConverter.class).in(Singleton.class);
            bind(ValuesCollector.class).to(BasicValuesCollector.class).in(Singleton.class);
            bind(EntityOperationsProvider.class).to(DefaultEntityOperationsProvider.class).in(Singleton.class);
            bind(EntitySearchProvider.class).to(DefaultEntitySearchProvider.class).in(Singleton.class);
            bind(EntityManager.class).to(DefaultEntityManager.class).in(Singleton.class);
            bind(DataSource.class).toInstance(pgDataSource());
        }
    });

    @Before
    public void setup () {
        injector.injectMembers(this);
    }

    public abstract DataSource pgDataSource();
}
