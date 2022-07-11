package com.github.OMEN44.simpleSQL.entities.row;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.entities.HasTable;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class Row implements HasTable {
    private final List<Cell> cells;
    private Table parentTable;

    public Row(Cell... cell) {
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

    @Override
    public void setParentTable(Table table) {
        for (Cell cell : cells) {
            cell.setParentTable(table);
        }
        this.parentTable = table;
    }

    /**
     * @return The rest of the cells in that row.
     */
    public List<Cell> getCells() {
        return this.cells;
    }

    /**
     * @return returns the length of the row
     */
    public int length() {
        return this.cells.size();
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

    @Override
    public void writeToDatabase(Connector connector) {

    }
}
