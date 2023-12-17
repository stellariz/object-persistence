package ru.nsu.ccfit.orm.core.sql.query.common.element;

public record Limit(int value) {
    public Limit(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Limit value must be positive.");
        } else {
            this.value = value;
        }
    }
}