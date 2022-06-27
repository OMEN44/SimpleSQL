package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class UniqueColumn extends CreateColumn {

    public UniqueColumn(String name, Datatype dataType, @Nonnull Object defaultValue, boolean primaryKey, boolean foreignKey, Cell... cells) {
        super(name, dataType, defaultValue, true, true, primaryKey, foreignKey, cells);
    }

    public UniqueColumn(String name, Datatype dataType, @Nonnull Object defaultValue, Cell... cells) {
        super(name, dataType, defaultValue, true, true, false, false, cells);
    }
}
