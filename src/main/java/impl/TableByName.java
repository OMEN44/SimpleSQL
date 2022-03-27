package impl;

import connectors.Connector;
import entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TableByName implements Table {
    private final String NAME;
    private PrimaryKey PRIMARY_COLUMN;
    private final List<Column> COLUMNS;

    public TableByName(Connector connector, String name) {
        this.NAME = name;

        Connection conn = null;
        PreparedStatement ps = null;
        this.COLUMNS = new ArrayList<>();
        try {
            conn = connector.getSQLConnection();
            if (conn != null) {
                //get table if it is mysql
                if (connector.connectorType() == Connector.ConnectionType.MYSQL) {
                    ps = conn.prepareStatement("DESCRIBE " + name);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getString("Key").equalsIgnoreCase("pri")) {
                            if (rs.getString("Type").endsWith(")"))
                                this.PRIMARY_COLUMN = new CreatePrimaryColumn(
                                        rs.getString("Field"),
                                        Datatype.valueOf(rs.getString("Type").split("\\(")[0]
                                                .toUpperCase())
                                );
                            else
                                this.PRIMARY_COLUMN = new CreatePrimaryColumn(
                                        rs.getString("Field"),
                                        Datatype.valueOf(rs.getString("Type"))
                                );
                        } else {
                            if (rs.getString("Type").endsWith(")"))
                                this.COLUMNS.add(new CreateColumn(
                                        rs.getString("Field"),
                                        Datatype.valueOf(rs.getString("Type").split("\\(")[0]
                                                .toUpperCase())
                                ));
                            else
                                this.COLUMNS.add(new CreateColumn(
                                        rs.getString("Field"),
                                        Datatype.valueOf(rs.getString("Type"))
                                ));
                        }
                    }
                }
                //get table if it is sqlite
                else {
                    ps = conn.prepareStatement("PRAGMA table_info(" + name + ");");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getString("pk").equalsIgnoreCase("1")) {
                            if (rs.getString("type").endsWith(")"))
                                this.PRIMARY_COLUMN = new CreatePrimaryColumn(
                                        rs.getString("name"),
                                        Datatype.valueOf(rs.getString("type").split("\\(")[0]
                                                .toUpperCase())
                                );
                            else
                                this.PRIMARY_COLUMN = new CreatePrimaryColumn(
                                        rs.getString("name"),
                                        Datatype.valueOf(rs.getString("type"))
                                );
                        } else {
                            if (rs.getString("type").endsWith(")"))
                                this.COLUMNS.add(new CreateColumn(
                                        rs.getString("name"),
                                        Datatype.valueOf(rs.getString("type").split("\\(")[0]
                                                .toUpperCase())

                                ));
                            else
                                this.COLUMNS.add(new CreateColumn(
                                        rs.getString("name"),
                                        Datatype.valueOf(rs.getString("type"))
                                ));
                        }
                    }
                }
            }
        } catch (SQLSyntaxErrorException e) {
            System.err.println("No such table with name: " + connector.getDatabase().getName() + "." + name);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connector.disconnector(conn, ps);
        }
    }

    @Override
    public instanceType getObjectType() {
        return Entity.instanceType.TABLE;
    }

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public PrimaryKey getPrimaryColumn() {
        return this.PRIMARY_COLUMN;
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
        List<Column> columns = new ArrayList<>(this.COLUMNS);
        columns.add(this.PRIMARY_COLUMN);
        return columns;
    }

    @Override
    public List<Row> getRows() {
        List<Row> rows = new ArrayList<>();
        /*List<Cell> primaryCells = this.getPrimaryColumn().getCells();
        int index = primaryCells.size();
        for (Cell cell : primaryCells)
            rows.add(new RowByUniqueCell(cell.setParentTable(this)));*/

        return rows;
    }
}
