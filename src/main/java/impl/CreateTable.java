package impl;

import entities.Column;
import entities.PrimaryColumn;
import entities.Row;
import entities.Table;
import logger.EntityNotUniqueException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if (this.rows != null) return rows;
        else System.err.println("This table object was created and does not contain rows. Use TableByName#getRows");
        return null;
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
