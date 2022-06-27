package com.github.OMEN44.simpleSQL.entities.cell;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.logger.EntityNotUniqueException;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;
import com.github.OMEN44.simpleSQL.connectors.Datatype;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class CreateCell extends BasicCellImpl implements Cell {
    private final boolean IS_UNIQUE;
    private final boolean IS_PRIMARY;
    private final Datatype DATATYPE;
    private final Object DATA;
    private final Column COLUMN;
    private Table parentTable;
    private Cell rowIdentifier;

    public CreateCell(Datatype datatype, Object data, Column column, boolean isUnique, boolean isPrimary) {
        super(datatype, data);
        this.DATATYPE = datatype;
        this.DATA = data;
        this.COLUMN = column;
        this.IS_UNIQUE = isUnique;
        this.IS_PRIMARY = isPrimary;
    }

    public CreateCell(Datatype datatype, Object data, Column column) {
        super(datatype, data);
        this.DATATYPE = datatype;
        this.DATA = data;
        this.COLUMN = column;
        this.IS_UNIQUE = false;
        this.IS_PRIMARY = false;
    }

    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.CELL;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (parentTable == null) {
            throw new TableUnassignedException("Parent table has not been set for this object. ", this.getEntityType());
        }
        return parentTable;
    }

    @Override
    public void setParentTable(Table table) {
        this.parentTable = table;
    }

    @Nonnull
    @Override
    public Column getColumn() {
        return this.COLUMN;
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
                "SELECT " + this.COLUMN.getName() + " FROM " + this.parentTable.getName()
        );
        return table.getColumns().get(0);
    }

    @Override
    public boolean isUnique() {
        return IS_UNIQUE;
    }

    @Override
    public boolean isPrimary() {
        return IS_PRIMARY;
    }

    @Nullable
    @Override
    public Object getData() {
        return DATA;
    }

    @Nonnull
    @Override
    public Datatype getDatatype() {
        return DATATYPE;
    }

    @Override
    public String toString() {
        assert this.DATA != null;
        return "Datatype | " + this.DATATYPE +
                "\nData     | " + this.DATA +
                "\nColumn   | " + this.COLUMN.getName();
    }
}
