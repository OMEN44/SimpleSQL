package entities;

import entities.HasTable;
import entities.Cell;
import internal.Datatype;

import java.util.List;

@SuppressWarnings("unused")
public interface Column extends HasTable {

    /**
     * @return Gets a list of all the cells in a column. Returns null if there are no rows.
     */
    List<Cell> getCells();

    /**
     * @return Gets the name of the column.
     */
    String getName();

    /**
     * @return Gets the {@link Datatype} of the row.
     */
    Datatype getDatatype();

    /**
     * @return True if the column defaults to null.
     */
    boolean isNotNull();

    /**
     * @return Gets the default value of the column. If the column is null this will always be null.
     */
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
}
