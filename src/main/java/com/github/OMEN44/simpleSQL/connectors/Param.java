package com.github.OMEN44.simpleSQL.connectors;

import com.github.OMEN44.simpleSQL.entities.column.Column;

/**
 * This class is a simple object that contains the values required for the WHERE clause.
 */
@SuppressWarnings("unused")
public record Param(Column column, Object value) {
    public Column getColumn() {
        return column;
    }
    public Object getValue() {
        return value;
    }
}
