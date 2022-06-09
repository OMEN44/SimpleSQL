package com.github.OMEN44.simpleSQL.entities;

import com.github.OMEN44.simpleSQL.impl.CreateColumn;
import com.github.OMEN44.simpleSQL.impl.CreateRow;
import com.github.OMEN44.simpleSQL.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * @param columns Columns to be set
     */
    void setColumns(Column... columns);

    /**
     * @return Returns all the rows in the table.
     */
    default List<Row> getRows() {
        List<Row> rows = new ArrayList<>();
        if (getColumns().size() == 0) {
            Logger.error("The table: " + getName() + " is empty");
            return new ArrayList<>();
        }
        int size = Objects.requireNonNull(getColumns().get(0).getCells()).size();
        for (int i = 0; i < size; i++) {
            List<Cell> cells = new ArrayList<>();
            for (Column col : getColumns()) {
                cells.add(Objects.requireNonNull(col.getCells()).get(i));
            }
            rows.add(new CreateRow(cells.toArray(new Cell[0])));
        }
        return rows;
    }

    /**
     * Updates the rows for the table
     */
    default Table setRows(Row... rows) {
        List<Column> columns = new ArrayList<>();
        int colNum = rows[0].getCells().size();
        List<List<Cell>> colsOfCells = new ArrayList<>();
        //organise rows into columns in a list
        for (int i = 0; i < colNum; i++) {
            List<Cell> cells = new ArrayList<>();
            for (Row row : rows) {
                cells.add(row.getCells().get(i));
            }
            colsOfCells.add(cells);
        }
        //create columns
        for (Cell cell : rows[0].getCells()) {
            columns.add(new CreateColumn(
                    cell.getColumn().getName(),
                    cell.getDatatype(),
                    cell.getColumn().getDefaultValue(),
                    cell.getColumn().isNotNull(),
                    cell.getColumn().isUnique(),
                    cell.getColumn().isPrimary()
            ));
        }
        //add cells to columns
        for (int i = 0; i < columns.size(); i++) {
            columns.set(i, columns.get(i).setCells(colsOfCells.get(i).toArray(new Cell[0])));
        }
        setColumns(columns.toArray(new Column[0]));
        return this;
    }
}
