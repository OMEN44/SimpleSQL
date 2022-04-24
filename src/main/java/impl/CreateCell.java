package impl;

import com.github.OMEN44.boxer.Boxer;
import connectors.Connector;
import connectors.Datatype;
import entities.*;
import logger.EntityNotUniqueException;
import logger.TableUnassignedException;

import javax.annotation.Nonnull;

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

    @Nonnull
    @Override
    public Entity setParentTable(Table table) {
        this.parentTable = table;
        return this;
    }

    @Nonnull
    @Override
    public Column getColumn() {
        return this.COLUMN;
    }

    @Nonnull
    @Override
    public Row getRow() {
        return null;
    }

    @Nonnull
    @Override
    public Cell setRowIdentifier(@Nonnull Cell uniqueCell) throws EntityNotUniqueException {
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
        Table table = connector.executeQuery(
                "SELECT " + this.COLUMN.getName() + " FROM " + this.parentTable.getName()
        );
        System.out.println(table.getColumns().get(0).getCells().size());
        return null;
    }

    @Override
    public boolean isUnique() {
        return IS_UNIQUE;
    }

    @Override
    public boolean isPrimary() {
        return IS_PRIMARY;
    }

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
        Boxer boxer;
        if (this.DATA == null)
            boxer = new Boxer("NULL");
        else
            boxer = new Boxer(this.DATA.toString());
        String title = this.COLUMN.getName();
        if (this.parentTable != null)
            title = this.parentTable.getName() + "." + title;
        boxer.addTitle(title).addFooter(this.DATATYPE.toString());
        boxer.buildBox();
        return boxer.getOutput();
    }
}