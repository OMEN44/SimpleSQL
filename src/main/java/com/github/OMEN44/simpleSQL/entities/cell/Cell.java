package com.github.OMEN44.simpleSQL.entities.cell;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.row.Row;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.logger.EntityNotUniqueException;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @apiNote This interface is for full cells it extends {@link BasicCell} by adding the cell's column, table and uniqueness
 */
@SuppressWarnings("unused")
public interface Cell extends BasicCell {
    /**
     * @return Gets the column that this cell belongs to.
     */
    @Nonnull
    Column getColumn();

    /**
     * @return Gets the other cells in the same row.
     */
    @Nonnull
    default Row getRow(Connector connector) throws TableUnassignedException, EntityNotUniqueException {
        if (getParentTable() == null)
            throw new TableUnassignedException("This object requires a table to preform this action");
        if (getRowIdentifier() == null)
            throw new EntityNotUniqueException("This cell needs a unique identifier");
        ResultTable table = connector.executeQuery(
                "SELECT * FROM " + getParentTable().getName() + " WHERE " +
                        getRowIdentifier().getColumn().getName() + "=?",
                getRowIdentifier().getData()
        );
        List<Cell> cells = new ArrayList<>();
        for (Column c : table.getColumns())
            cells.add(Objects.requireNonNull(c.getCells()).get(0));
        return new Row(cells.toArray(new Cell[0]));
    }

    @Nonnull
    Cell setRowIdentifier(@Nonnull UniqueCell uniqueCell) throws EntityNotUniqueException;

    @Nullable
    Cell getRowIdentifier();

    /**
     * @apiNote This method is recommended instead of {@link Cell#getColumn()}
     * @return Gets the full column instead of the column name.
     */
    @Nonnull
    Column getFullColumn(Connector connector) throws TableUnassignedException;

    /**
     * @return {@code true} if the cell is in a unique column and {@code false} otherwise.
     */
    boolean isUnique();

    /**
     * @return {@code true} if the cell is in a primary column and {@code false} otherwise.
     */
    boolean isPrimary();

    @Override
    default void writeToDatabase(Connector connector) {

    }
}
