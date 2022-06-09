package com.github.OMEN44.simpleSQL.impl;

import com.github.OMEN44.simpleSQL.entities.*;
import com.github.OMEN44.simpleSQL.logger.EntityNotUniqueException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CreateTable implements Table {
    private final String NAME;
    private List<Column> columns;
    private PrimaryColumn PRIMARY_COLUMN;

    public CreateTable(String name, PrimaryColumn primaryColumn, Column... columns) throws EntityNotUniqueException {
        this.NAME = name;
        if (primaryColumn.isPrimary())
            this.PRIMARY_COLUMN = primaryColumn;
        else
            throw new EntityNotUniqueException("The primary column specified is not primary.");
        this.columns = new ArrayList<>(Arrays.asList(columns));
    }

    public CreateTable(String name, Column... columns) {
        this.NAME = name;
        this.columns = new ArrayList<>(Arrays.asList(columns));
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
        return this.PRIMARY_COLUMN;
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
        List<Column> columns = new ArrayList<>(this.columns);
        if (this.PRIMARY_COLUMN != null)
            columns.add(this.PRIMARY_COLUMN);
        return columns;
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
