package simpleSQL.impl;

import simpleSQL.connectors.Connector;
import simpleSQL.connectors.dbProfiles.Database;
import simpleSQL.entities.*;
import simpleSQL.logger.SimpleSQLException;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class TableByName implements Table {
    private final String NAME;
    private final List<Column> COLUMNS;
    private PrimaryColumn primaryColumn;
    private List<Row> rows;

    public TableByName(Connector connector, String name) {
        boolean toggleDebug = false;
        if (connector.isDebugMode()) {
            connector.debugMode(false, true);
            toggleDebug = true;
        }
        this.NAME = name;

        Connection conn = null;
        this.COLUMNS = new ArrayList<>();
        try {
            conn = connector.getSQLConnection();
            if (conn != null) {
                //get table if it is mysql
                if (connector.databaseType() == Database.DatabaseType.MYSQL) {
                    PreparedStatement ps = conn.prepareStatement("DESCRIBE " + name);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getString("Key").equalsIgnoreCase("pri")) {
                            Column col = new ColumnByName(connector, rs.getString("Field"), name);
                            this.primaryColumn = new CreatePrimaryColumn(
                                    col.getName(),
                                    col.getDatatype(),
                                    col.getDefaultValue(),
                                    col.isNotNull()
                            );
                        } else this.COLUMNS.add(new ColumnByName(connector, rs.getString("Field"), name));
                    }
                    ps.close();
                }
                //get table if it is sqlite
                else {
                    PreparedStatement ps = conn.prepareStatement("PRAGMA table_info(" + name + ");");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getString("pk").equalsIgnoreCase("1")) {
                            Column col = new ColumnByName(connector, rs.getString("name"), name);
                            this.primaryColumn = new CreatePrimaryColumn(
                                    col.getName(),
                                    col.getDatatype(),
                                    col.getDefaultValue(),
                                    col.isNotNull()
                            );
                        } else this.COLUMNS.add(new ColumnByName(connector, rs.getString("name"), name));
                    }
                    ps.close();
                }
                if (toggleDebug)
                    connector.debugMode(true, true);
            }
        } catch (SQLSyntaxErrorException e) {
            if (connector.isDebugMode())
                System.err.println("No such table with name: " + connector.getDatabase().getName() + "." + name);
        } catch (SQLException | SimpleSQLException e) {
            e.printStackTrace();
        } finally {
            Connector.disconnector(conn, null);
        }
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
        for (Column col : this.COLUMNS) {
            if (col.isUnique()) {
                columns.add(col);
            }
        }
        return columns;
    }

    @Override
    public List<Column> getColumns() {
        return new ArrayList<>(this.COLUMNS);
    }

    @Override
    public List<Row> getRows() {
        this.rows = new ArrayList<>();
        int size = Objects.requireNonNull(getColumns().get(0).getCells()).size();
        for (int i = 0; i < size; i++) {
            List<Cell> cells = new ArrayList<>();
            for (Column col : getColumns()) {
                cells.add(col.getCells().get(i));
            }
            this.rows.add(new CreateRow(cells.toArray(new Cell[0])));
        }
        return rows;
    }

    @Override
    public Table setRows(Row... rows) {
        this.rows = Arrays.stream(rows).toList();
        return this;
    }
}
