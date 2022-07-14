package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class ForeignKey extends UniqueColumn {

    private String tableReferencing;
    private String colReferencing;
    private String onDelete;
    private String onUpdate;

    public ForeignKey(String name, Datatype dataType, @Nonnull Object defaultValue, boolean primaryKey,
                      String colReferencing, String tableReferencing, Cell... cells) {
        super(name, dataType, defaultValue, primaryKey, true, cells);
        setColReferencing(colReferencing);
        setTableReferencing(tableReferencing);
    }

    public ForeignKey(String name, Datatype dataType, @Nonnull Object defaultValue, String colReferencing,
                      String tableReferencing, Cell... cells) {
        super(name, dataType, defaultValue, false, true, cells);
        setColReferencing(colReferencing);
        setTableReferencing(tableReferencing);
    }

    public ForeignKey(String name, Datatype datatype, String colReferencing, String tableReferencing) {
        super(name, datatype, null, false, true);
        setColReferencing(colReferencing);
        setTableReferencing(tableReferencing);
    }

    public String getTableReferencing() {
        return tableReferencing;
    }

    public ForeignKey setTableReferencing(String referencedTableNames) {
        this.tableReferencing = referencedTableNames;
        return this;
    }

    public String getColReferencing() {
        return colReferencing;
    }

    public ForeignKey setColReferencing(String colReferencing) {
        this.colReferencing = colReferencing;
        return this;
    }
}
