package com.github.OMEN44.simpleSQL.entities.column;

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
public interface Column extends HasTable {

    /**
     * @return Gets the name of the column.
     */
    @Nonnull
    String getName();

    /**
     * @return Gets the {@link Datatype} of the row.
     */
    @Nonnull
    Datatype getDatatype();

    /**
     * @return True if the column defaults to null.
     */
    boolean isNotNull();

    /**
     * @return Gets the default value of the column. If the column is null this will always be null.
     */
    @Nullable
    Object getDefaultValue();

    /**
     * @return If true the column is unique. There can be multiple unique columns in a table, this also includes the
     * primary column.
     */
    boolean isUnique();

    /**
     * @return There can only be one primary column.
     */
    boolean isPrimary();

    /**
     * @return true if the column is a foreign key
     */
    boolean isForeignKey();

    /**
     * @return Gets a list of all the cells in a column. Returns null if there are no rows.
     */
    @Nonnull
    List<Cell> getCells();

    /**
     * @param cells Cells to be added to the column
     * @return The column with its new cells
     */
    @Nonnull
    Column setCells(Cell... cells);

    /**
     * @param cells Cells to be added to the column
     */
    void addCell(Cell... cells);

    /**
     * @return Converts a column to a primary column
     */
    @Nonnull
    PrimaryKey toPrimaryColumn();

    /**
     * @return Converts a column to a foreign key
     */
    @Nonnull
    ForeignKey toForeignKey();

    /**
     * @return Converts a column to a unique column
     */
    @Nonnull
    UniqueColumn toUniqueColumn();
}
