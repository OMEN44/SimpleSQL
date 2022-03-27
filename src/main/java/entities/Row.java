package entities;

import logger.EntityNotUniqueException;
import logger.TableUnassignedException;

import java.util.List;

public interface Row extends HasTable {
    /**
     * @param primaryCell A cell that is in a primary column and has a table assigned to it.
     * @return The rest of the cells in that row.
     * @throws TableUnassignedException This will be thrown if either the cell or its parent column don't have an
     * assigned table.
     * @throws EntityNotUniqueException This will be thrown if the cell is not from a primary or unique column.
     */
    List<Cell> getCells(Cell primaryCell) throws TableUnassignedException, EntityNotUniqueException;
}
