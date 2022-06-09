package com.github.OMEN44.simpleSQL.entities;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.logger.EntityNotUniqueException;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    Row getRow(Connector connector) throws TableUnassignedException, EntityNotUniqueException;

    @Nonnull
    Cell setRowIdentifier(Cell uniqueCell) throws EntityNotUniqueException;

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
}
