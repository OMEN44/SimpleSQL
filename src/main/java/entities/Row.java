package entities;

import logger.EntityNotUniqueException;
import logger.TableUnassignedException;

import java.util.List;

@SuppressWarnings("unused")
public interface Row extends HasTable {
    /**
     * @return The rest of the cells in that row.
     */
    List<Cell> getCells();
}
