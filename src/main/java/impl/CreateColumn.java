package impl;

import connectors.Datatype;
import entities.*;
import logger.TableUnassignedException;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateColumn implements Column {
    private final Datatype DATATYPE;
    private final String NAME;
    private final Object DEFAULT_VALUE;
    private final boolean NOT_NULL;
    private final boolean IS_UNIQUE;
    private final boolean PRIMARY_KEY;
    private List<Cell> cells;
    private Table parentTable;

    public CreateColumn(String name, Datatype dataType,
                        Object defaultValue, boolean notNull, boolean isUnique, boolean primaryKey) {
        this.DATATYPE = dataType;
        this.NAME = name;
        this.DEFAULT_VALUE = defaultValue;
        this.NOT_NULL = notNull;
        if (primaryKey)
            this.IS_UNIQUE = true;
        else
            this.IS_UNIQUE = isUnique;
        this.PRIMARY_KEY = primaryKey;
    }

    public CreateColumn(String name, Datatype dataType) {
        this.DATATYPE = dataType;
        this.NAME = name;
        this.DEFAULT_VALUE = null;
        this.NOT_NULL = false;
        this.IS_UNIQUE = false;
        this.PRIMARY_KEY = false;
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

    @Nonnull
    @Override
    public Entity setParentTable(Table table) {
        this.parentTable = table;
        return this;
    }

    @Override
    public List<Cell> getCells() {
        return this.cells;
    }

    @Nonnull
    @Override
    public Column setCells(Cell... cells) {
        this.cells = Arrays.stream(cells).toList();
        return this;
    }

    @Nonnull
    @Override
    public Column addCell(Cell... cells) {
        this.cells.addAll(Arrays.asList(cells));
        return this;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.NAME;
    }

    @Nonnull
    @Override
    public Datatype getDatatype() {
        return this.DATATYPE;
    }

    @Override
    public boolean isNotNull() {
        return this.NOT_NULL;
    }

    @Override
    public Object getDefaultValue() {
        return this.DEFAULT_VALUE;
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
    public String toString() {
        return this.getName();
    }
}
