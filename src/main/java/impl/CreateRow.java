package impl;

import connectors.Connector;
import entities.Cell;
import entities.HasTable;
import entities.Row;
import entities.Table;
import logger.TableUnassignedException;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateRow implements Row {
    private final List<Cell> cells;
    private Table parentTable;

    public CreateRow(Cell... cell) {
        this.cells = Arrays.stream(cell).toList();
    }

    @Override
    public instanceType getObjectType() {
        return instanceType.ROW;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (this.parentTable == null) throw new TableUnassignedException("No table has been assigned to this row");
        else return this.parentTable;
    }

    @Override
    public HasTable setParentTable(Table table) {
        this.parentTable = table;
        return this;
    }

    @Override
    public void write(Connector conn) throws TableUnassignedException {

    }

    @Override
    public List<Cell> getCells() {
        return this.cells;
    }
}
