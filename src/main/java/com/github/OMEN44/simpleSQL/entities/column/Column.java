package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.HasTable;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @apiNote This object mainly contains a list of cells and similarly to cells also contains uniqueness constraints
 */
@SuppressWarnings("unused")
public abstract class Column implements HasTable {

    /**
     * @return Gets the name of the column.
     */
    @Nonnull
    public abstract String getName();

    /**
     * @return Gets the {@link Datatype} of the row.
     */
    @Nonnull
    public abstract Datatype getDatatype();

    /**
     * @return True if the column defaults to null.
     */
    public abstract boolean isNotNull();

    /**
     * @return Gets the default value of the column. If the column is null this will always be null.
     */
    @Nullable
    public abstract Object getDefaultValue();

    /**
     * @return If true the column is unique. There can be multiple unique columns in a table, this also includes the
     * primary column.
     */
    public abstract boolean isUnique();

    /**
     * @return There can only be one primary column.
     */
    public abstract boolean isPrimary();

    /**
     * @return true if the column is a foreign key
     */
    public abstract boolean isForeignKey();

    public abstract String getColReferencing();

    public abstract String getTableReferencing();

    /**
     * @return Gets a list of all the cells in a column. Returns null if there are no rows.
     */
    @Nonnull
    public abstract List<Cell> getCells();

    /**
     * @param cells Cells to be added to the column
     * @return The column with its new cells
     */
    @Nonnull
    public abstract Column setCells(Cell... cells);

    /**
     * @param cells Cells to be added to the column
     */
    public abstract void addCell(Cell... cells);

    /**
     * @return Converts a column to a primary column
     */
    @SuppressWarnings("all")
    @Nonnull
    public PrimaryKey toPrimaryColumn() {
        return new PrimaryKey(
                getName(),
                getDatatype(),
                getDefaultValue(),
                isForeignKey(),
                getCells().toArray(new Cell[0])
        );
    }

    /**
     * @return Converts a column to a foreign key
     */
    @SuppressWarnings("all")
    @Nonnull
    public ForeignKey toForeignKey() {
        return new ForeignKey(
                getName(),
                getDatatype(),
                getDefaultValue(),
                isPrimary(),
                getColReferencing(),
                getTableReferencing(),
                getCells().toArray(new Cell[0])
        );
    }

    /**
     * @return Converts a column to a unique column
     */
    @SuppressWarnings("all")
    @Nonnull
    public UniqueColumn toUniqueColumn() {
        return new UniqueColumn(
                getName(),
                getDatatype(),
                getDefaultValue(),
                isPrimary(),
                isForeignKey(),
                getCells().toArray(new Cell[0])
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName())
                .append("\n");
        sb.append("=".repeat(this.getName().length()))
                .append("\n");
        for (int i = 0; i < getCells().size(); i++) {
            sb.append(getCells().get(i).getData())
                    .append("\n");
        }
        sb.append("=".repeat(this.getName().length()));
        return sb.toString();
    }

    @Override
    public void writeToDatabase(Connector connector) {

    }
}
