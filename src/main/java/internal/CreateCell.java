package internal;

import entities.HasTable;
import entities.Table;
import entities.Cell;
import entities.Column;
import logger.TableUnassignedException;

@SuppressWarnings("unused")
public class CreateCell extends BasicCellImpl implements Cell {
    private final boolean IS_UNIQUE;
    private final boolean IS_PRIMARY;
    private final Datatype DATATYPE;
    private final Object DATA;
    private final Column COLUMN;
    private Table parentTable;

    public CreateCell(Datatype datatype, Object data, Column column, boolean isUnique, boolean isPrimary) {
        super(datatype, data);
        this.DATATYPE = datatype;
        this.DATA = data;
        this.COLUMN = column;
        this.IS_UNIQUE = isUnique;
        this.IS_PRIMARY = isPrimary;
    }

    @Override
    public instanceType getObjectType() {
        return instanceType.CELL;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (parentTable == null) {
            throw new TableUnassignedException("Parent table has not been set for this object. ", this.getObjectType());
        }
        return parentTable;
    }

    @Override
    public HasTable setParentTable(Table table) {
        this.parentTable = table;
        return this;
    }

    @Override
    public Column getColumn() {
        return this.COLUMN;
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

    @Override
    public Datatype getDatatype() {
        return DATATYPE;
    }
}
