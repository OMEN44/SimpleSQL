package com.github.OMEN44.simpleSQL.entities.table;

import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.column.*;
import com.github.OMEN44.simpleSQL.entities.row.Row;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.row.CreateRow;
import com.github.OMEN44.simpleSQL.logger.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public interface Table extends Entity {
    /**
     * @return Get the name of the table.
     */
    @Nonnull
    String getName();

    /**
     * @return Gets all the columns in the table.
     */
    @Nonnull
    List<Column> getColumns();

    /**
     * @return Returns all the candidate keys, this means all the columns that can be used to uniquely identify a row.
     */
    @Nullable
    default List<UniqueColumn> getCandidateKeys() {
        List<UniqueColumn> columns = new ArrayList<>();
        if (getColumns().size() == 0) {
            Logger.error("No columns found in table " + getName() + ". Returning empty list...");
            return new ArrayList<>();
        }
        for (Column col : getColumns()) {
            if (col.isUnique() || col.isForeignKey() || col.isPrimary()) {
                columns.add(col.toUniqueColumn());
            }
        }
        return columns;
    }

    /**
     * @return Get the primary columns of the table.
     */
    @Nullable
    default PrimaryKey getPrimaryKey() {
        if (getColumns().size() == 0) {
            Logger.error("No columns found in table " + getName() + ". Returning empty list...");
            return null;
        }
        for (Column col : getColumns()) {
            if (col.isPrimary()) {
                return col.toPrimaryColumn();
            }
        }
        return null;
    }

    /**
     * @return Get any unique columns of the table.
     * This does not include the primary column and foreign keys to get these use {@link Table#getAlternateKeys()}.
     */
    @Nullable
    default List<UniqueColumn> getUniqueColumns() {
        List<UniqueColumn> columns = new ArrayList<>();
        if (getColumns().size() == 0) {
            Logger.error("No columns found in table " + getName() + ". Returning empty list...");
            return new ArrayList<>();
        }
        for (Column col : getColumns()) {
            if (col.isUnique()) {
                columns.add(col.toUniqueColumn());
            }
        }
        return columns;
    }

    /**
     * @return returns all the candidate keys excluding the chosen primary key
     * this can be used to get a list of columns that can replace the primary key
     */
    @Nullable
    default List<UniqueColumn> getAlternateKeys() {
        List<UniqueColumn> columns = new ArrayList<>();
        if (getColumns().size() == 0) {
            Logger.error("No columns found in table " + getName() + ". Returning empty list...");
            return new ArrayList<>();
        }
        for (Column col : getColumns()) {
            if (col.isUnique() || col.isForeignKey()) {
                if (!col.isPrimary())
                    columns.add(col.toUniqueColumn());
            }
        }
        return columns;
    }

    /**
     * @return returns all the foreign keys in the table
     */
    @Nullable
    default List<ForeignKey> getForeignKeys() {
        List<ForeignKey> columns = new ArrayList<>();
        if (getColumns().size() == 0) {
            Logger.error("No columns found in table " + getName() + ". Returning empty list...");
            return new ArrayList<>();
        }
        for (Column col : getColumns()) {
            if (col.isForeignKey()) {
                columns.add(col.toForeignKey());
            }
        }
        return columns;
    }

    /**
     * @param columns Columns to be set
     */
    void setColumns(Column... columns);

    /**
     * @return Returns all the rows in the table.
     */
    @Nonnull
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
    @Nonnull
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
                    cell.getColumn().isPrimary(),
                    cell.getColumn().isForeignKey()
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
