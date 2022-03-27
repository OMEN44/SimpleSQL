package impl;

import connectors.Connector;
import entities.Cell;
import entities.Column;
import entities.HasTable;
import entities.Table;
import logger.MissingColumnException;
import logger.TableUnassignedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("unused")
public class ColumnByName implements Column {
    private final Datatype DATATYPE;
    private final String NAME;
    private final Object DEFAULT_VALUE;
    private final boolean NOT_NULL;
    private final boolean IS_UNIQUE;
    private final boolean PRIMARY_KEY;
    private List<Cell> cells;
    private Table parentTable;

    public ColumnByName(Connector connector, String name, Table table) throws MissingColumnException {
        this.NAME = name;
        this.parentTable = table;

        Connection conn = null;
        PreparedStatement ps = null;
        Column pc = null;

        try {
            conn = connector.getSQLConnection();
            if (connector.connectorType() == Connector.ConnectionType.MYSQL) {
                ps = conn.prepareStatement("DESCRIBE " + table.getName());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getString("Field").equals(this.NAME)) {
                        if (rs.getString("Type").endsWith(")"))
                            pc = new CreateColumn(
                                    rs.getString("Field"),
                                    Datatype.valueOf(rs.getString("Type").split("\\(")[0]
                                            .toUpperCase()),
                                    rs.getObject("Default"),
                                    rs.getString("Null").equals("YES"),
                                    rs.getString("Key").equals("UNI"),
                                    rs.getString("Key").equals("PRI")
                            );
                        else
                            pc = new CreateColumn(
                                    rs.getString("Field"),
                                    Datatype.valueOf(rs.getString("Type")),
                                    rs.getObject("Default"),
                                    rs.getString("Null").equals("YES"),
                                    rs.getString("Key").equals("UNI"),
                                    rs.getString("Key").equals("PRI")
                            );
                    }
                }
            }
            //get table if it is sqlite
            else {
                ps = conn.prepareStatement("PRAGMA table_info(" + table.getName() + ");");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getString("name").equals(this.NAME)) {
                        if (rs.getString("type").endsWith(")"))
                            pc = new CreateColumn(
                                    rs.getString("name"),
                                    Datatype.valueOf(rs.getString("type").split("\\(")[0]
                                            .toUpperCase()),
                                    rs.getObject("dflt_value"),
                                    rs.getString("notnull").equals("1"),
                                    rs.getString("pk").equals("1"),
                                    rs.getString("pk").equals("1")
                            );
                        else
                            pc = new CreateColumn(
                                    rs.getString("name"),
                                    Datatype.valueOf(rs.getString("type")),
                                    rs.getObject("dflt_value"),
                                    rs.getString("notnull").equals("1"),
                                    rs.getString("pk").equals("1"),
                                    rs.getString("pk").equals("1")
                            );
                    }
                }
            }
            pc.setParentTable(table);
        } catch (NullPointerException e) {
            throw new MissingColumnException("Column " + name + " was not found in table " + table.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connector.disconnector(conn, ps);
        }

        assert pc != null;
        this.DATATYPE = pc.getDatatype();
        this.DEFAULT_VALUE = pc.getDefaultValue();
        this.NOT_NULL = pc.isNotNull();
        if (pc.isPrimary())
            this.IS_UNIQUE = true;
        else
            this.IS_UNIQUE = pc.isUnique();
        this.PRIMARY_KEY = pc.isUnique();
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
    public void write(Connector conn) throws TableUnassignedException {

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
}
