package com.github.OMEN44.simpleSQL.entities;

import java.util.List;

/**
 * Rows are a list of cells that can have a range of constrains and has no identifying name.
 */
@SuppressWarnings("unused")
public interface Row extends HasTable {
    /**
     * @return The rest of the cells in that row.
     */
    List<Cell> getCells();
}
