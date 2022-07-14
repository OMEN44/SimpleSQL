package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateColumn extends Column {
    private final boolean IS_UNIQUE;
    private final boolean PRIMARY_KEY;
    private final boolean FOREIGN_KEY;
    private Datatype datatype;
    private String name;
    private Object defaultValue;
    private List<Cell> cells;
    private Table parentTable;
    private boolean notNull;
    private String colReferencing;
    private String tableReferencing;

    public CreateColumn(String name, Datatype dataType, Object defaultValue,
                        boolean notNull, boolean isUnique, boolean primaryKey, boolean foreignKey,
                        String colReferencing, String tableReferencing, Cell... cells) {
        this.datatype = dataType;
        this.name = name;
        this.defaultValue = defaultValue;
        this.notNull = notNull;
        if (primaryKey || foreignKey)
            this.IS_UNIQUE = true;
        else
            this.IS_UNIQUE = isUnique;
        this.PRIMARY_KEY = primaryKey;
        this.FOREIGN_KEY = foreignKey;
        this.colReferencing = colReferencing;
        this.tableReferencing = tableReferencing;
        this.cells = new ArrayList<>(Arrays.stream(cells).toList());
    }
    public CreateColumn(String name, Datatype dataType, Object defaultValue,
                        boolean notNull, boolean isUnique, boolean primaryKey, boolean foreignKey, Cell... cells) {
        this.datatype = dataType;
        this.name = name;
        this.defaultValue = defaultValue;
        this.notNull = notNull;
        if (primaryKey || foreignKey)
            this.IS_UNIQUE = true;
        else
            this.IS_UNIQUE = isUnique;
        this.PRIMARY_KEY = primaryKey;
        this.FOREIGN_KEY = foreignKey;
        this.cells = new ArrayList<>(Arrays.stream(cells).toList());
    }

    public CreateColumn(String name, Datatype dataType, Cell... cells) {
        this.datatype = dataType;
        this.name = name;
        this.defaultValue = null;
        this.notNull = false;
        this.IS_UNIQUE = false;
        this.PRIMARY_KEY = false;
        this.FOREIGN_KEY = false;
        this.cells = new ArrayList<>(Arrays.stream(cells).toList());
    }

    public String getColReferencing() {
        return colReferencing;
    }

    public String getTableReferencing() {
        return tableReferencing;
    }

    public void setColReferencing(String colReferencing) {
        this.colReferencing = colReferencing;
    }

    public void setTableReferencing(String tableReferencing) {
        this.tableReferencing = tableReferencing;
    }

    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.COLUMN;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (parentTable == null) {
            throw new TableUnassignedException("Parent table has not been set for this object.", this.getEntityType());
        }
        return parentTable;
    }

    @Override
    public void setParentTable(Table table) {
        this.parentTable = table;
    }

    @Nonnull
    @Override
    public List<Cell> getCells() {
        if (this.cells == null) return new ArrayList<>();
        return this.cells;
    }

    @Nonnull
    @Override
    public Column setCells(Cell... cells) {
        this.cells = Arrays.stream(cells).toList();
        return this;
    }

    @Override
    public void addCell(Cell... cells) {
        this.cells.addAll(Arrays.asList(cells));
    }

    @Override
    public boolean isUnique() {
        return this.IS_UNIQUE;
    }

    @Override
    public boolean isPrimary() {
        return this.PRIMARY_KEY;
    }

    @Override
    public boolean isForeignKey() {
        return this.FOREIGN_KEY;
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
    public Datatype getDatatype() {
        return this.datatype;
    }

    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }

    @Override
    public boolean isNotNull() {
        return this.notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void writeToDatabase() {

    }
}
