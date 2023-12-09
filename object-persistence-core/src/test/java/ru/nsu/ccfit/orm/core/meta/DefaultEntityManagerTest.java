package ru.nsu.ccfit.orm.core.meta;

import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

class DefaultEntityManagerTest {

    private static final PGSimpleDataSource pgTestDataSource;

    static {
        pgTestDataSource = new PGSimpleDataSource();
        pgTestDataSource.setURL("jdbc:postgresql://localhost:5432/postgres");
        pgTestDataSource.setDatabaseName("postgres");
        pgTestDataSource.setUser("postgres");
        pgTestDataSource.setPassword("password");
    }

    @Test
    public void createSinglePerson() {
    }

}