package com.github.OMEN44.simpleSQL.entities.table;

import com.github.OMEN44.simpleSQL.connectors.Condition;
import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.entities.FromDatabase;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.column.ColumnByName;
import com.github.OMEN44.simpleSQL.logger.IllegalConditionException;
import com.github.OMEN44.simpleSQL.logger.Logger;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class TableFromDatabase extends Table implements FromDatabase {
    private final Connector CONNECTOR;
    private final String NAME;
    private List<Column> columns;

    protected TableFromDatabase(Connector connector, String name) throws MissingColumnException {
        this.CONNECTOR = connector;
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

    @Override
    public void drop() {
        this.CONNECTOR.executeUpdate("DROP TABLE `" + this.NAME + "`;");
    }

    public void delete(Condition... conditions) throws IllegalConditionException {
        this.delete(this.CONNECTOR, this.NAME, conditions);
    }

    public void deleteAll() throws IllegalConditionException {
        this.delete(this.CONNECTOR, this.NAME);
    }
}
