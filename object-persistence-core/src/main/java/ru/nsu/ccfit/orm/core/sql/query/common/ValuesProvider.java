package ru.nsu.ccfit.orm.core.sql.query.common;

import java.util.List;

public interface ValuesProvider {

    List<?> provideValues();

}
