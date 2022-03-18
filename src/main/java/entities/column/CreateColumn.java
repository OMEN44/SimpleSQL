package entities.column;

import entities.HasTable;
import entities.Table;
import entities.cell.Cell;
import internal.Datatype;
import logger.TableUnassignedException;

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

    @Override
    public instanceType getObjectType() {
        return instanceType.COLUMN;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (parentTable == null) {
            throw new TableUnassignedException("Parent table has not been set for this object.", this.getObjectType());
        }
        return parentTable;
    }

    @Override
    public HasTable setParentTable(Table table) {
        this.parentTable = table;
        return this;
    }

    @Override
    public List<Cell> getCells() {
        return this.cells;
    }

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public Datatype getDatatype() {
        return this.DATATYPE;
    }

    @Override
    public boolean isNull() {
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
}
