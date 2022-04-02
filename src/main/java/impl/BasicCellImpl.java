package impl;

import connectors.Connector;
import entities.HasTable;
import entities.Table;
import entities.BasicCell;
import entities.Cell;
import entities.Column;
import logger.Boxer;
import logger.TableUnassignedException;

@SuppressWarnings("unused")
public class BasicCellImpl implements BasicCell {
    private final Datatype DATATYPE;
    private final Object DATA;

    public BasicCellImpl(Datatype datatype, Object data) {
        this.DATATYPE = datatype;
        this.DATA = data;
    }

    @Override
    public instanceType getObjectType() {
        return instanceType.CELL;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        System.out.println("This object cannot be written to a table. Use the method toCell to do this.");
        return null;
    }

    @Override
    public HasTable setParentTable(Table table) {
        System.out.println("This object cannot be written to a table. Use the method toCell to do this.");
        return null;
    }

    @Override
    public void write(Connector conn) {
        System.out.println("This object cannot be written to a table. Use the method toCell to do this.");
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

    @Override
    public String toString() {
        Boxer boxer = new Boxer(this.DATA.toString()).addFooter(this.DATATYPE.toString());
        boxer.buildBox();
        return boxer.getOutput();
    }
}
