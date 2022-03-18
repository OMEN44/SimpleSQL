package entities.cell;

import entities.HasTable;
import internal.Datatype;

/**
 * BasicCell is the base for most of this library. <br>
 * This class represents a cell in a database and contains the minimum information and can be created independent of
 * the database. This is the superclass for {@link Cell} which build onto to the functionality of cells, {@link Cell}
 * is not independent of the database.
 */
@SuppressWarnings("unused")
public interface BasicCell extends HasTable {
    /**
     * @return Returns the data contained by this cell as an object.
     */
    Object getData();

    /**
     * @return Returns the datatype of the cell.
     */
    Datatype getDatatype();
}
