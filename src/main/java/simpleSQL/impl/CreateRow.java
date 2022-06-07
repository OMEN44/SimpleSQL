package simpleSQL.impl;

import simpleSQL.entities.*;
import simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
        StringBuilder top = new StringBuilder("|");
        StringBuilder middle = new StringBuilder("|");
        StringBuilder bottom = new StringBuilder("|");
        for (Cell cell : this.cells) {
            top.append(cell.getColumn().getName());
            middle.append("-".repeat(cell.getColumn().getName().length()));
            bottom.append(cell.getData());
            int diff = cell.getColumn().getName().length() - Objects.requireNonNull(cell.getData()).toString().length();
            if (diff < 0) {
                //data longer
                top.append(" ".repeat(-diff));
                middle.append("-".repeat(-diff));
            } else if (diff > 0) {
                //name longer
                bottom.append(" ".repeat(diff));
            }
            top.append(" |");
            middle.append("-|");
            bottom.append(" |");
        }
        return top + "\n" + middle + "\n" + bottom + "\n";
    }
}
