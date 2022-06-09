package com.github.OMEN44.simpleSQL.impl;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.Database;
import com.github.OMEN44.simpleSQL.entities.Cell;
import com.github.OMEN44.simpleSQL.entities.Column;
import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.Table;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;
import com.github.OMEN44.simpleSQL.logger.SimpleSQLException;
import com.github.OMEN44.simpleSQL.logger.TableUnassignedException;
import simpleSQL.entities.*;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class ColumnByName implements Column {
    private final Datatype DATATYPE;
    private final String NAME;
    private final Object DEFAULT_VALUE;
    private final boolean NOT_NULL;
    private final boolean IS_UNIQUE;
    private final boolean PRIMARY_KEY;
    private final List<Cell> cells;
    private Table parentTable;

    public ColumnByName(Connector connector, String columnName, String tableName) throws MissingColumnException {
        this.NAME = columnName;

        Connection conn = null;
        PreparedStatement ps = null;
        Column pc = null;

        try {
            conn = connector.getSQLConnection();
            if (connector.databaseType() == Database.DatabaseType.MYSQL) {
                ps = conn.prepareStatement("DESCRIBE " + tableName);
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
                                    Datatype.valueOf(rs.getString("Type").toUpperCase()),
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
                ps = conn.prepareStatement("PRAGMA table_info(" + tableName + ");");
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
                                    Datatype.valueOf(rs.getString("type").toUpperCase()),
                                    rs.getObject("dflt_value"),
                                    rs.getString("notnull").equals("1"),
                                    rs.getString("pk").equals("1"),
                                    rs.getString("pk").equals("1")
                            );
                    }
                }
            }
            //get cells for column:
            Table results = connector.executeQuery("SELECT `" + columnName + "` FROM " + tableName);
            assert pc != null;
            pc.setCells(Objects.requireNonNull(results.getColumns().get(0).getCells()).toArray(new Cell[0]));
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new MissingColumnException("Column " + columnName + " was not found in table " + tableName);
        } catch (SQLException | SimpleSQLException e) {
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
        this.cells = pc.getCells();
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

    @Override
    public List<Cell> getCells() {
        return this.cells;
    }

    @Nonnull
    @Override
    public Column setCells(Cell... cells) {
        System.err.println("The cells of this column cannot be changed!");
        return this;
    }

    @Nonnull
    @Override
    public Column addCell(Cell... cells) {
        System.err.println("The cells of this column cannot be changed!");
        return this;
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.NAME)
                .append("\n");
        sb.append("=".repeat(this.getName().length()))
                .append("\n");
        for (int i = 0; i < Objects.requireNonNull(getCells()).size() - 1; i++) {
            sb.append(getCells().get(i).getData())
                    .append("\n");
        }
        sb.append("=".repeat(this.getName().length()));
        return sb.toString();
    }
}
