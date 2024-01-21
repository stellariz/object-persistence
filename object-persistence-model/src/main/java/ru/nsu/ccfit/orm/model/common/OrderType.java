package ru.nsu.ccfit.orm.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderType {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private String type;
}
