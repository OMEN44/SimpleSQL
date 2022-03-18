package entities;

import entities.column.Column;
import logger.EntityNotUniqueException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateTable implements Table {
    private final String name;
    private final Column primaryColumn;
    private final List<Column> columns;

    public CreateTable(String name, Column primaryColumn, Column... columns) throws EntityNotUniqueException {
        this.name = name;
        if (primaryColumn.isPrimary())
            this.primaryColumn = primaryColumn;
        else
            throw new EntityNotUniqueException("The primary column specified is not primary.");
        this.columns = new ArrayList<>(Arrays.asList(columns));
    }

    @Override
    public instanceType getObjectType() {
        return instanceType.TABLE;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Column getPrimaryColumn() {
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
        return this.columns;
    }
}
