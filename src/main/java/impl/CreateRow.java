package impl;

import entities.*;
import logger.TableUnassignedException;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateRow implements Row {
    private final List<Cell> cells;
    private Table parentTable;

    public CreateRow(Cell... cell) {
        this.cells = Arrays.stream(cell).toList();
    }

    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.ROW;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (this.parentTable == null) throw new TableUnassignedException("No table has been assigned to this row");
        else return this.parentTable;
    }

    @Nonnull
    @Override
    public Entity setParentTable(Table table) {
        for (Cell cell : cells) {
            cell.setParentTable(table);
        }
        this.parentTable = table;
        return this;
    }

    @Override
    public List<Cell> getCells() {
        return this.cells;
    }
}
