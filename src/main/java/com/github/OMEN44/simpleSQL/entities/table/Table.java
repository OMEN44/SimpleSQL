package com.github.OMEN44.simpleSQL.entities.table;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.column.*;
import com.github.OMEN44.simpleSQL.entities.row.Row;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.logger.Logger;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class Table implements Entity {
    /**
     * @return Get the name of the table.
     */
    @Nonnull
    public abstract String getName();

    /**
     * @return Gets all the columns in the table.
     */
    @Nonnull
    public abstract List<Column> getColumns();

    /**
     * @param name Name of target column
     * @return returns the column with the specified name if it is found else it will return null
     */
    @Nullable
    public Column getColumnByName(String name) {
        for (Column col : getColumns()) {
            if (col.getName().equals(name))
                return col;
        }
        return null;
    }

    /**
     * @return Returns all the candidate keys, this means all the columns that can be used to uniquely identify a row.
     */
    @Nullable
    public List<UniqueColumn> getCandidateKeys() {
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
    public PrimaryKey getPrimaryKey() {
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
    public List<UniqueColumn> getUniqueColumns() {
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
    public List<UniqueColumn> getAlternateKeys() {
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
    public List<ForeignKey> getForeignKeys() {
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
    public abstract void setColumns(Column... columns);

    /**
     * @return Returns all the rows in the table.
     */
    @Nonnull
    public List<Row> getRows() {
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
            rows.add(new Row(cells.toArray(new Cell[0])));
        }
        return rows;
    }

    /**
     * Updates the rows for the table
     */
    @Nonnull
    public Table setRows(Row... rows) {
        for (Row row : rows) {
            for (int i = 0; i < row.length(); i++) {
                Column target = getColumnByName(row.getCells().get(i).getColumn().getName());
                if (target != null) {
                    target.addCell(row.getCells().get(i));
                }
            }
        }
        for (Column col : getColumns()) {
            System.out.println(col);
        }
        /*

        List<Column> columns = new ArrayList<>();
        List<List<Cell>> colsOfCells = new ArrayList<>();
        //organise rows into columns in a list
        for (int j = 0; j < rows[0].length(); j++) {
            List<Cell> cells = new ArrayList<>();
            for (Row row : rows) {
                cells.add(row.getCells().get(j));
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
        setColumns(columns.toArray(new Column[0]));*/
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Result Table: \n");
        List<Row> rows = getRows();
        //get the biggest sizes:
        List<Integer> colWidth = new ArrayList<>();
        for (Column col : getColumns()) {
            colWidth.add(col.getName().length());
            for (Cell cell : Objects.requireNonNull(col.getCells())) {
                int dataLength;
                if (cell.getData() == null)
                    dataLength = 4;
                else
                    dataLength = cell.getData().toString().length();
                if (colWidth.get(getColumns().indexOf(col)) < dataLength) {
                    colWidth.set(getColumns().indexOf(col), cell.getData().toString().length());
                }
            }
        }
        //find full width
        int width = colWidth.size() * 2;
        for (Integer i : colWidth) {
            width = width + i;
        }
        //create table
        //get horizontal wall
        StringBuilder hWall = new StringBuilder();
        for (int i = 0; i < colWidth.size(); i++) {
            String name = getColumns().get(i).getName();
            hWall.append("|").append("-".repeat(colWidth.get(i) + 1));
        }
        sb.append(hWall.append("|\n"));

        //makes header
        for (int i = 0; i < colWidth.size(); i++) {
            String name = getColumns().get(i).getName();
            sb.append("|").append(name).append(" ".repeat(colWidth.get(i) - name.length() + 1));
        }
        sb.append("|\n").append(hWall);

        for (Row row : getRows()) {
            int i = 0;
            StringBuilder line = new StringBuilder();
            for (Cell cell : row.getCells()) {
                Object data;
                if (cell.getData() == null)
                    data = "NULL";
                else
                    data = cell.getData();
                line.append("|").append(data)
                        .append(" ".repeat(colWidth.get(i) - data.toString().length() + 1));
                i++;
            }
            sb.append(line).append("|\n");
        }
        return sb.append(hWall).toString();
    }

    @Override
    public void writeToDatabase(Connector connector) {
        StringBuilder columns = new StringBuilder();
        for (Column col : getColumns()) {
            StringBuilder sb = new StringBuilder();
            sb.append(", \n  ").append("`").append(col.getName()).append("` ")
                    .append(Datatype.toString(col.getDatatype()));
            if (col.isNotNull())
                sb.append(" NOT NULL");
            if (col.getDefaultValue() != null)
                sb.append(" DEFAULT '").append(col.getDefaultValue()).append("'");
            if (col.isUnique())
                sb.append(" UNIQUE");
            if (col.isPrimary())
                sb.append(",\n  PRIMARY KEY (`").append(col.getName()).append("`)");
            if (col.isForeignKey()) {
                ForeignKey fk = col.toForeignKey();
                sb.append(",\n  CONSTRAINT `FK_").append(getName()).append("_").append(col.getName())
                        .append("` FOREIGN KEY (`").append(col.getName()).append("`) REFERENCES `")
                        .append(fk.getReferencedTableNames()).append("` (`").append(fk.getReferencedColumnName())
                        .append("`)");
            }
            columns.append(sb);
        }
        connector.executeUpdate("CREATE TABLE IF NOT EXISTS `" + getName() + "` (" +
                columns.substring(2) + "\n)");
        for (Row row : getRows()) {
            row.setParentTable(this);
            StringBuilder sb = new StringBuilder("INSERT INTO `" + getName() + "` (");
            List<String> s = new ArrayList<>();
            for (Cell cell : row.getCells()) {
                if (getColumnByName(cell.getColumn().getName()) != null) {
                    sb.append(cell.getColumn().getName()).append(", ");
                    if (cell.getData() == null) s.add(null);
                    else s.add(cell.getData().toString());
                }
            }
            sb.replace(sb.length() - 2, sb.length(), ") VALUES (?")
                    .append(", ?".repeat(Math.max(0, s.size() - 1))).append(")");
            connector.executeUpdate(sb.toString(), s.toArray(new Object[0]));
        }
    }
}
