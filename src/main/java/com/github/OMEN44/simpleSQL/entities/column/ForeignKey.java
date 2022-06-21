package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class ForeignKey extends UniqueColumn implements Column {

    public ForeignKey(String name, Datatype dataType, @Nonnull Object defaultValue, boolean primaryKey, Cell... cells) {
        super(name, dataType, defaultValue, primaryKey, true, cells);
    }

    public ForeignKey(String name, Datatype dataType, @Nonnull Object defaultValue, Cell... cells) {
        super(name, dataType, defaultValue, false, true, cells);
    }
}
