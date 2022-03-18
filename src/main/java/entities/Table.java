package entities;

import entities.column.Column;

import java.util.List;

@SuppressWarnings("unused")
public interface Table extends Entity {
    /**
     * @return Get the name of the table.
     */
    String getName();

    /**
     * @return Get the primary column of the table.
     */
    Column getPrimaryColumn();

    /**
     * @return Get any unique columns of the table. This includes the primary column.
     */
    List<Column> getUniqueColumns();

    /**
     * @return Gets all the columns in the table.
     */
    List<Column> getColumns();
}
