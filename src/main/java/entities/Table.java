package entities;

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
    PrimaryColumn getPrimaryColumn();

    /**
     * @return Get any unique columns of the table. This includes the primary column.
     */
    List<Column> getUniqueColumns();

    /**
     * @return Gets all the columns in the table.
     */
    List<Column> getColumns();

    /**
     * @return Returns all the rows in the table.
     */
    List<Row> getRows();

    /**
     * Updates the rows for the table
     */
    Table setRows(Row... rows);
}
