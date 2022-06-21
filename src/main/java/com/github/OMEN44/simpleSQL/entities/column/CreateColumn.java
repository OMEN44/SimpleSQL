package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CreateColumn implements Column {
    private final boolean IS_UNIQUE;
    private final boolean PRIMARY_KEY;
    private final boolean FOREIGN_KEY;
    private Datatype datatype;
    private String name;
    private Object defaultValue;
    private List<Cell> cells;
    private Table parentTable;
    private boolean notNull;

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

    @Nonnull
    @Override
    public PrimaryKey toPrimaryColumn() {
        return new PrimaryKey(
                this.name,
                this.datatype,
                this.defaultValue,
                this.FOREIGN_KEY,
                this.cells.toArray(new Cell[0])
        );
    }

    @Nonnull
    @Override
    public ForeignKey toForeignKey() {
        return new ForeignKey(
                this.name,
                this.datatype,
                this.defaultValue,
                this.PRIMARY_KEY,
                this.cells.toArray(new Cell[0])
        );
    }

    @Nonnull
    @Override
    public UniqueColumn toUniqueColumn() {
        return new UniqueColumn(
                this.name,
                this.datatype,
                this.defaultValue,
                this.PRIMARY_KEY,
                this.FOREIGN_KEY,
                this.cells.toArray(new Cell[0])
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name)
                .append("\n");
        sb.append("=".repeat(this.getName().length()))
                .append("\n");
        for (int i = 0; i < Objects.requireNonNull(getCells()).size(); i++) {
            sb.append(getCells().get(i).getData())
                    .append("\n");
        }
        sb.append("=".repeat(this.getName().length()));
        return sb.toString();
    }
}
