package com.github.OMEN44.simpleSQL.entities.table;

import com.github.OMEN44.simpleSQL.entities.column.Column;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateTable extends Table {
    private String name;
    private List<Column> columns;

    public CreateTable(String name, Column... columns) {
        this.name = name;
        this.columns = new ArrayList<>();
        for (Column col : columns) {
            col.setParentTable(this);
            this.columns.add(col);
        }
    }

    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.TABLE;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public List<Column> getColumns() {
        return new ArrayList<>(this.columns);
    }

    @Override
    public void setColumns(Column... columns) {
        this.columns = Arrays.stream(columns).toList();
    }
}
