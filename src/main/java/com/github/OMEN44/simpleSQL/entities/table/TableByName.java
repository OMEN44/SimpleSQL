package com.github.OMEN44.simpleSQL.entities.table;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.column.ColumnByName;
import com.github.OMEN44.simpleSQL.logger.Logger;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class TableByName extends Table {
    private final String NAME;
    private List<Column> columns;

    public TableByName(Connector connector, String name) throws MissingColumnException {
        boolean toggleDebug = false;
        if (Logger.isDebugMode()) {
            Logger.debugMode(false, true);
            toggleDebug = true;
        }

        this.NAME = name;
        //get cells from database
        ResultTable table = connector.executeQuery("SELECT * FROM " + name);
        List<Column> cols = table.getColumns();
        //add column constraints and data
        this.columns = new ArrayList<>();
        for (Column col : cols)
            this.columns.add(new ColumnByName(connector, col.getName(), name));
        if (toggleDebug)
            Logger.debugMode(true, true);
    }

    @Nonnull
    @Override
    public InstanceType getEntityType() {
        return InstanceType.TABLE;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.NAME;
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
