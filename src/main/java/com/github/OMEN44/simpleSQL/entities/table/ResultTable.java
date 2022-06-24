package com.github.OMEN44.simpleSQL.entities.table;

import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.row.Row;
import com.github.OMEN44.simpleSQL.logger.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class ResultTable implements Table {
    private final String QUERY;
    private final List<Column> COLUMNS;
    private final int colIndexMax;
    private final int rowIndexMax;
    private int colIndex = -1;
    private int rowIndex = 0;

    public ResultTable(String query, Column... columns) {
        this.QUERY = query;
        this.COLUMNS = new ArrayList<>();
        for (Column col : columns) {
            col.setParentTable(this);
            this.COLUMNS.add(col);
        }
        this.colIndexMax = columns.length;
        if (this.colIndexMax != 0)
            this.rowIndexMax = columns[0].getCells().size();
        else
            this.rowIndexMax = 0;
    }

    /**
     * @return Returns the query that was used to get this table of data.
     */
    public String getQuery() {
        return QUERY;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public int getColIndexMax() {
        return colIndexMax;
    }

    public int getRowIndexMax() {
        return rowIndexMax;
    }

    public boolean isEmpty() {
        if (this.COLUMNS.size() == 0) return true;
        else return this.COLUMNS.get(0).getCells().size() != 0;
    }

    public Cell get() {
        return this.COLUMNS.get(this.colIndex).getCells().get(this.rowIndex);
    }

    public Column getCol() {
        if (this.colIndex == -1) {
            Logger.error("The cursor must be moved onto the table use ResultTable.next() to do this. Returning null...");
            return null;
        }
        if (this.colIndexMax == 0) {
            Logger.error("The result table being queried is empty. Returning null...");
            return null;
        }
        return this.COLUMNS.get(this.colIndex);
    }

    public Row getRow() {
        return this.getRows().get(this.rowIndex);
    }

    //iteration controllers

    public boolean next() {
        if (this.colIndexMax == 0 || this.rowIndexMax == 0) return false;
        colIndex++;
        if (this.colIndex == this.colIndexMax) {
            rowIndex++;
            if (this.rowIndex == this.rowIndexMax)
                return false;
            colIndex = 0;
        }
        return true;
    }

    public boolean next(int i) {
        if (this.colIndexMax == 0 || this.rowIndexMax == 0) return false;
        colIndex+=i;
        if (this.colIndex == this.colIndexMax) {
            rowIndex++;
            if (this.rowIndex == this.rowIndexMax)
                return false;
            colIndex = 0;
        }
        return true;
    }

    public boolean nextCol() {
        if (this.colIndex == this.colIndexMax - 1) return false;
        this.colIndex++;
        return true;
    }

    public boolean nextCol(int i) {
        if (this.colIndex == this.colIndexMax - 1) return false;
        this.colIndex+=i;
        return true;
    }

    public ResultTable nextRow() {
        this.rowIndex++;
        return this;
    }

    public ResultTable nextRow(int reps) {
        this.rowIndex += reps;
        return this;
    }



    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.TABLE;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Result Table";
    }

    @Nonnull
    @Override
    public List<Column> getColumns() {
        return this.COLUMNS;
    }

    @Override
    public void setColumns(Column... columns) {
        Logger.error("This table instance cannot be updated. No columns were added to result set.");
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
}
