package simpleSQL.impl;

import simpleSQL.entities.*;
import simpleSQL.logger.EntityNotUniqueException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CreateTable implements Table {
    private final String NAME;
    private final List<Column> COLUMNS;
    private List<Row> rows;
    private PrimaryColumn PRIMARY_COLUMN;

    public CreateTable(String name, PrimaryColumn primaryColumn, Column... columns) throws EntityNotUniqueException {
        this.NAME = name;
        if (primaryColumn.isPrimary())
            this.PRIMARY_COLUMN = primaryColumn;
        else
            throw new EntityNotUniqueException("The primary column specified is not primary.");
        this.COLUMNS = new ArrayList<>(Arrays.asList(columns));
    }

    public CreateTable(String name, Column... columns) {
        this.NAME = name;
        this.COLUMNS = new ArrayList<>(Arrays.asList(columns));
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
        for (Column col : this.COLUMNS) {
            if (col.isUnique()) {
                columns.add(col);
            }
        }
        return columns;
    }

    @Override
    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<>(this.COLUMNS);
        if (this.PRIMARY_COLUMN != null)
            columns.add(this.PRIMARY_COLUMN);
        return columns;
    }

    @Override
    public List<Row> getRows() {
        this.rows = new ArrayList<>();
        int size = Objects.requireNonNull(getColumns().get(0).getCells()).size();
        for (int i = 0; i < size; i++) {
            List<Cell> cells = new ArrayList<>();
            for (Column col : getColumns()) {
                cells.add(col.getCells().get(i));
            }
            this.rows.add(new CreateRow(cells.toArray(new Cell[0])));
        }
        return rows;
    }

    @Override
    public Table setRows(Row... rows) {
        this.rows = Arrays.stream(rows).toList();
        return this;
    }

    @Override
    public String toString() {
        return "table";
    }
}
