package simpleSQL.impl;

import simpleSQL.connectors.Connector;
import simpleSQL.connectors.dbProfiles.Database;
import simpleSQL.entities.Column;
import simpleSQL.entities.PrimaryColumn;
import simpleSQL.entities.Row;
import simpleSQL.entities.Table;
import simpleSQL.logger.MissingColumnException;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class TableByName implements Table {
    private final String NAME;
    private final List<Column> COLUMNS;
    private PrimaryColumn primaryColumn;
    private List<Row> rows;

    public TableByName(Connector connector, String name) {
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
            }
        } catch (SQLSyntaxErrorException e) {
            if (connector.isDebugMode())
                System.err.println("No such table with name: " + connector.getDatabase().getName() + "." + name);
        } catch (SQLException | MissingColumnException e) {
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
        System.err.println("unfinished");
        return rows;
    }

    @Override
    public Table setRows(Row... rows) {
        this.rows = Arrays.stream(rows).toList();
        return this;
    }
}
