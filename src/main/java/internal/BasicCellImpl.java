package internal;

import entities.HasTable;
import entities.Table;
import entities.BasicCell;
import entities.Cell;
import entities.Column;
import logger.TableUnassignedException;

@SuppressWarnings("unused")
public class BasicCellImpl implements BasicCell {
    private final Datatype DATATYPE;
    private final Object DATA;
    private Table table;

    public BasicCellImpl(Datatype datatype, Object data) {
        this.DATATYPE = datatype;
        this.DATA = data;
    }

    public BasicCellImpl(Datatype datatype, Object data, Table table) {
        this.DATATYPE = datatype;
        this.DATA = data;
        this.table = table;
    }

    @Override
    public instanceType getObjectType() {
        return instanceType.CELL;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (this.table == null) {
            throw new TableUnassignedException("Table was not assigned while creating this cell.");
        } else {
            return this.table;
        }
    }

    @Override
    public HasTable setParentTable(Table table) {
        this.table = table;
        return this;
    }

    @Override
    public Object getData() {
        return this.DATA;
    }

    @Override
    public Datatype getDatatype() {
        return this.DATATYPE;
    }

    public Cell toCell(Column column, boolean isUnique, boolean isPrimary) {
        return new CreateCell(this.DATATYPE, this.DATA, column, isUnique, isPrimary);
    }
}
