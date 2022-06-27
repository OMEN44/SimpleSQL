package com.github.OMEN44.simpleSQL.entities.cell;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.column.CreateColumn;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.logger.EntityNotUniqueException;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class UniqueCell extends CreateCell implements Cell {
    private Datatype datatype;
    private Object data;
    private Column column;
    private Table parentTable;
    private UniqueCell rowIdentifier;
    private boolean primary;

    public UniqueCell(Datatype datatype, Object data, Column column) {
        super(datatype, data, column, true, column.isPrimary());
        this.datatype = datatype;
        this.data = data;
        this.column = column;
        this.primary = column.isPrimary();
    }

    public UniqueCell(Datatype datatype, Object data, String columnName) {
        super(datatype, data, new CreateColumn(columnName, datatype), true, false);
    }

    @Nonnull
    @Override
    public Column getColumn() {
        return this.column;
    }

    @Nonnull
    @Override
    public Cell setRowIdentifier(@Nonnull UniqueCell uniqueCell) throws EntityNotUniqueException {
        if (!uniqueCell.isUnique() && !uniqueCell.isPrimary() &&
                !uniqueCell.getColumn().isUnique() && !uniqueCell.getColumn().isPrimary())
            throw new EntityNotUniqueException("Cell provided for row identifier must be either unique or primary");
        this.rowIdentifier = uniqueCell;
        return this;
    }

    @Nullable
    @Override
    public Cell getRowIdentifier() {
        return this.rowIdentifier;
    }

    @Nonnull
    @Override
    public Column getFullColumn(Connector connector) throws TableUnassignedException {
        if (this.parentTable == null)
            throw new TableUnassignedException("This object requires a table to preform this action");
        ResultTable table = connector.executeQuery(
                "SELECT " + this.column.getName() + " FROM " + this.parentTable.getName()
        );
        return table.getColumns().get(0);
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    @Nullable
    @Override
    public Object getData() {
        return this.data;
    }

    @Nonnull
    @Override
    public Datatype getDatatype() {
        return this.datatype;
    }
}
