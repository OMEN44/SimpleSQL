package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.table.Table;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ForeignKey extends UniqueColumn implements Column {

    private List<Table> referencedTables;

    public ForeignKey(String name, Datatype dataType, @Nonnull Object defaultValue, boolean primaryKey, Cell... cells) {
        super(name, dataType, defaultValue, primaryKey, true, cells);
    }

    public ForeignKey(String name, Datatype dataType, @Nonnull Object defaultValue, Cell... cells) {
        super(name, dataType, defaultValue, false, true, cells);
    }

    public List<Table> getReferencedTables() {
        if (this.referencedTables == null) return new ArrayList<>();
        return referencedTables;
    }

    public void setReferencedTables(Table... referencedTables) {
        this.referencedTables = Arrays.stream(referencedTables).toList();
    }
}
