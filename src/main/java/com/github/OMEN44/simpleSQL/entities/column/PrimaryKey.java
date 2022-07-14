package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class PrimaryKey extends UniqueColumn {

    public PrimaryKey(String name, Datatype dataType, @Nonnull Object defaultValue, boolean foreignKey, Cell... cells) {
        super(name, dataType, defaultValue, true, foreignKey, cells);
    }

    public PrimaryKey(String name, Datatype dataType, @Nonnull Object defaultValue, Cell... cells) {
        super(name, dataType, defaultValue, true, false, cells);
    }

    public PrimaryKey(String name, Datatype datatype) {
        super(name, datatype, null, true, false);
    }
}
