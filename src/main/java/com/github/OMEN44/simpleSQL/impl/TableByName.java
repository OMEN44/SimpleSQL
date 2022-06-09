package com.github.OMEN44.simpleSQL.impl;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.entities.*;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;
import simpleSQL.entities.*;
import com.github.OMEN44.simpleSQL.logger.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.github.OMEN44.simpleSQL.logger.Logger.log;

@SuppressWarnings("unused")
public class TableByName implements Table {
    private final String NAME;
    private List<Column> columns;
    private PrimaryColumn primaryColumn;

    public TableByName(Connector connector, String name) throws MissingColumnException {
        boolean toggleDebug = false;
        if (Logger.isDebugMode()) {
            Logger.debugMode(false, true);
            toggleDebug = true;
        }

        this.NAME = name;
        //get cells from database
        List<Column> cols;
        Table table = connector.executeQuery("SELECT * FROM " + name);
        cols = table.getColumns();

        //add column constraints and data
        this.columns = new ArrayList<>();
        log(cols.size());
        for (Column col : cols) {
            Column byName = new ColumnByName(connector, col.getName(), name);
            this.columns.add(new CreateColumn(
                    col.getName(),
                    byName.getDatatype(),
                    byName.getDefaultValue(),
                    byName.isNotNull(),
                    byName.isUnique(),
                    byName.isPrimary()
            ).setCells(Objects.requireNonNull(byName.getCells()).toArray(new Cell[0])));
        }

        if (toggleDebug)
            Logger.debugMode(true, true);
    }

    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.TABLE;
    }

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public PrimaryColumn getPrimaryColumn() {
        return this.primaryColumn;
    }

    @Override
    public List<Column> getUniqueColumns() {
        List<Column> columns = new ArrayList<>();
        for (Column col : this.columns) {
            if (col.isUnique()) {
                columns.add(col);
            }
        }
        return columns;
    }

    @Override
    public List<Column> getColumns() {
        return new ArrayList<>(this.columns);
    }

    @Override
    public void setColumns(Column... columns) {
        this.columns = Arrays.stream(columns).toList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Table name: " + this.NAME + "\n");
        List<Row> rows = getRows();
        //get the biggest sizes:
        List<Integer> colWidth = new ArrayList<>();
        for (Column col : getColumns()) {
            colWidth.add(col.getName().length());
            for (Cell cell : Objects.requireNonNull(col.getCells())) {
                if (colWidth.get(getColumns().indexOf(col)) <
                        Objects.requireNonNull(cell.getData()).toString().length()) {
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
        hWall.append("|\n");

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
                line.append("|").append(Objects.requireNonNull(cell.getData()))
                        .append(" ".repeat(colWidth.get(i) - cell.getData().toString().length() + 1));
                i++;
            }
            sb.append(line).append("|\n").append(hWall);
        }
        return sb.toString();
    }
}