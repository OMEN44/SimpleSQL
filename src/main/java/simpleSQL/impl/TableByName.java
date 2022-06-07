package simpleSQL.impl;

import simpleSQL.connectors.Connector;
import simpleSQL.entities.Cell;
import simpleSQL.entities.Column;
import simpleSQL.entities.PrimaryColumn;
import simpleSQL.entities.Table;
import simpleSQL.logger.Logger;
import simpleSQL.logger.MissingColumnException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static simpleSQL.logger.Logger.log;

@SuppressWarnings("unused")
public class TableByName implements Table {
    private final String NAME;
    private List<Column> columns;
    private PrimaryColumn primaryColumn;

    public TableByName(Connector connector, String name) throws MissingColumnException {
        boolean toggleDebug = false;
        if (Logger.isDebugMode()) {
            Logger.debugMode(false, true);
            toggleDebug = true;
        }

        this.NAME = name;
        //get cells from database
        List<Column> cols;
        Table table = connector.executeQuery("SELECT * FROM " + name);
        cols = table.getColumns();

        //add column constraints and data
        this.columns = new ArrayList<>();
        log(cols.size());
        for (Column col : cols) {
            Column byName = new ColumnByName(connector, col.getName(), name);
            this.columns.add(new CreateColumn(
                    col.getName(),
                    byName.getDatatype(),
                    byName.getDefaultValue(),
                    byName.isNotNull(),
                    byName.isUnique(),
                    byName.isPrimary()
            ).setCells(Objects.requireNonNull(byName.getCells()).toArray(new Cell[0])));
        }

        if (toggleDebug)
            Logger.debugMode(true, true);
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
        return new ArrayList<>(this.columns);
    }

    @Override
    public void setColumns(Column... columns) {
        this.columns = Arrays.stream(columns).toList();
    }
}
