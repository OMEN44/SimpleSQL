package com.github.OMEN44.simpleSQL.entities.column;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.row.Row;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.logger.Logger;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class ColumnByName implements Column {
    private final String NAME;
    private final Datatype DATATYPE;
    private final Object DEFAULT_VALUE;
    private final boolean NOT_NULL;
    private final boolean IS_UNIQUE;
    private final boolean PRIMARY_KEY;
    private final boolean FOREIGN_KEY;
    private final List<Cell> CELLS;
    private String referencedColumn;
    private String referencedTable;
    private Table PARENT_TABLE;

    public ColumnByName(Connector connector, String columnName, String tableName) throws MissingColumnException {
        boolean toggleDebug = false;
        if (Logger.isDebugMode()) {
            Logger.debugMode(false, true);
            toggleDebug = true;
        }

        //set name and cells
        this.NAME = columnName;
        List<Cell> cells = new ArrayList<>();
        ResultTable r = connector.executeQuery("SELECT " + columnName + " FROM " + tableName);
        if (r.getColIndexMax() != 0) {
            r.nextCol();
            this.CELLS = r.getCol().getCells();
        } else {
            throw new MissingColumnException("The column " + columnName + " was not found in " + tableName);
        }

        Datatype datatype = Datatype.OBJECT;
        Object defaultValue = null;
        boolean notNull = false;
        boolean isUnique = false;
        boolean primaryKey = false;
        boolean foreignKey = false;

        //get constraints
        switch (connector.databaseType()) {
            case MYSQL -> {
                ResultTable rt = connector.executeQuery("show create table `" + tableName + "`");
                rt.next(2);
                for (String s : rt.get().getData().toString().split("\n")) {
                    if (s.startsWith(columnName, 3)) {
                        if (s.contains("NOT NULL")) notNull = true;
                        datatype = Datatype.datatypeOf(
                                s.substring(s.lastIndexOf("`") + 2, s.indexOf(" ", s.lastIndexOf("`") + 2))
                        );
                        if (s.contains("DEFAULT") && s.contains("'"))
                            defaultValue = s.substring(s.indexOf("'"), s.lastIndexOf("'"));
                        else defaultValue = null;
                    }
                    if (s.startsWith("  PRIMARY KEY") && s.contains(columnName)) {
                        primaryKey = true;
                        isUnique = true;
                    }
                    if (s.startsWith("  UNIQUE KEY") && s.contains(columnName)) isUnique = true;
                    if (s.startsWith(columnName, s.indexOf("FOREIGN KEY") + 14)) {
                        foreignKey = true;
                        this.referencedColumn = s.substring(s.lastIndexOf("(") + 2, s.lastIndexOf(")") - 1);
                        this.referencedTable = s.substring(s.indexOf("REFERENCES") + 12, s.lastIndexOf("(") - 2);
                    }
                }
            }
            case SQLITE -> {
                r = connector.executeQuery("PRAGMA table_info(" + tableName + ")");
                for (Row row : r.getRows()) {
                    if (row.getCells().get(1).getData().toString().equals(columnName)) {
                        datatype = Datatype.datatypeOf(row.getCells().get(2).getData().toString());
                        notNull = row.getCells().get(3).getData().toString().equals("1");
                        defaultValue = row.getCells().get(4).getData();
                        primaryKey = row.getCells().get(5).getData().toString().equals("1");
                        // get other unique constraints
                        ResultTable rt = connector.executeQuery("PRAGMA index_list(" + tableName + ")");
                        Column uniqueCols = rt.getColumns().get(1);
                        for (Row currentRow : rt.getRows()) {
                            ResultTable resultTable = connector.executeQuery(
                                    "PRAGMA index_info(" + currentRow.getCells().get(1).getData() + ")");
                            if (resultTable.getColumns().get(2).getCells().get(0).getData().equals(columnName)) {
                                isUnique = currentRow.getCells().get(2).getData().toString().equals("1");
                                foreignKey = currentRow.getCells().get(3).getData().toString().equals("c");
                                break;
                            }
                        }
                        // to get the rest of the constraints use: PRAGMA index_list(table_name); then use
                        // PRAGMA index_info(index_name); to find if the column being searched is the correct one.
                        // to get foreign key constraints use
                        // SELECT `table`, `from`, `to` FROM pragma_foreign_key_list('tracks');
                        break;
                    }
                }
            }
        }
        this.DATATYPE = datatype;
        this.DEFAULT_VALUE = defaultValue;
        this.NOT_NULL = notNull;
        this.IS_UNIQUE = isUnique;
        this.PRIMARY_KEY = primaryKey;
        this.FOREIGN_KEY = foreignKey;

        if (toggleDebug)
            Logger.debugMode(true, true);
    }

    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.COLUMN;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (PARENT_TABLE == null) {
            throw new TableUnassignedException("Parent table has not been set for this object.", this.getEntityType());
        }
        return PARENT_TABLE;
    }

    @Nonnull
    @Override
    public Entity setParentTable(Table table) {
        Logger.error("Cannot set the table of ColumnByName object.");
        return this;
    }

    @Nonnull
    @Override
    public List<Cell> getCells() {
        return this.CELLS;
    }

    @Nonnull
    @Override
    public Column setCells(Cell... cells) {
        Logger.error("The cells of ColumnByName cannot be updated.");
        return this;
    }

    @Override
    public void addCell(Cell... cells) {
        Logger.error("The cells of ColumnByName cannot be updated.");
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
    public boolean isForeignKey() {
        return this.FOREIGN_KEY;
    }

    @Nonnull
    @Override
    public ForeignKey toForeignKey() {
        return new ForeignKey(
                this.NAME,
                this.DATATYPE,
                this.DEFAULT_VALUE,
                this.PRIMARY_KEY,
                this.CELLS.toArray(new Cell[0])
        ).setReferencedTableNames(this.referencedTable).setReferencedColumnName(this.referencedColumn);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.NAME)
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
