package com.github.OMEN44.simpleSQL.impl;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.*;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class BasicCellImpl implements BasicCell {
    private final Datatype DATATYPE;
    private final Object DATA;

    public BasicCellImpl(Datatype datatype, Object data) {
        this.DATATYPE = datatype;
        this.DATA = data;
    }

    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.CELL;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        System.err.println("This object cannot be written to a table. Use the method toCell to do this.");
        return null;
    }

    @Nonnull
    @Override
    public Entity setParentTable(Table table) {
        System.err.println("This object cannot be written to a table. Use the method toCell to do this.");
        return this;
    }

    @Override
    public Object getData() {
        return this.DATA;
    }

    @Nonnull
    @Override
    public Datatype getDatatype() {
        return this.DATATYPE;
    }

    public Cell toCell(Column column, boolean isUnique, boolean isPrimary) {
        return new CreateCell(this.DATATYPE, this.DATA, column, isUnique, isPrimary);
    }

    @Override
    public String toString() {
        return "Datatype | " + this.DATATYPE +
                "\nData     | " + this.DATA;
    }
}
