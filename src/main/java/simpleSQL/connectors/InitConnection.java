package simpleSQL.connectors;

import simpleSQL.connectors.dbProfiles.Database;
import simpleSQL.connectors.dbProfiles.MySQL;
import simpleSQL.connectors.dbProfiles.SQLite;
import simpleSQL.entities.*;
import simpleSQL.impl.*;
import simpleSQL.logger.EntityNotUniqueException;
import simpleSQL.logger.Logger;
import simpleSQL.logger.TableUnassignedException;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class InitConnection implements Connector {

    private final Database DATABASE;
    private final Database.DatabaseType connType;
    public Connection connection;
    private Status status;
    private boolean debugMode = false;

    public InitConnection(Database database) {
        switch (database.getDatabaseType()) {
            case MYSQL -> {
                MySQL mySQL = (MySQL) database;
            }
            case SQLITE -> {
                SQLite sqLite = (SQLite) database;
            }
            default -> throw new IllegalArgumentException("Unknown database profile with name: " + database.getName());
        }
        this.connType = database.getDatabaseType();
        this.DATABASE = database;
        this.status = Status.READY;
    }

    @Override
    public Database.DatabaseType databaseType() {
        return this.connType;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public Database getDatabase() {
        return this.DATABASE;
    }

    @Override
    public void debugMode(boolean b) {
        this.debugMode = b;
    }

    @Override
    public boolean isDebugMode() {
        return this.debugMode;
    }

    @Override
    public Connection getSQLConnection() {
        switch (this.connType) {
            case MYSQL -> {
                MySQL mySQL = (MySQL) this.DATABASE;
                this.status = Status.CONNECTING;
                connection = null;
                try {
                    connection = DriverManager.getConnection(
                            this.DATABASE.getURL(),
                            mySQL.getUser(),
                            mySQL.getPassword()
                    );
                } catch (SQLException e) {
                    Logger.printSQLException(e);
                }
                this.status = Status.CONNECTED;
                return connection;
            }
            case SQLITE -> {
                SQLite sqLite = (SQLite) this.DATABASE;
                this.status = Status.CONNECTING;
                if (!sqLite.getDataFolder().exists()) {
                    try {
                        sqLite.getDataFolder().mkdir();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                File dataFolder = new File(sqLite.getDataFolder(), sqLite.getName() + ".db");
                if (!dataFolder.exists()) {
                    try {
                        dataFolder.createNewFile();
                    } catch (IOException e) {
                        System.err.println("File write error: " + sqLite.getName() + ".db");
                    }
                }
                try {
                    if (connection != null && !connection.isClosed()) {
                        this.status = Status.CONNECTED;
                        return connection;
                    }
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection(this.DATABASE.getURL());
                    this.status = Status.CONNECTED;
                    return connection;
                } catch (SQLException ex) {
                    System.err.println("SQLite exception on initialize");
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    System.err.println("You need the SQLite JDBC library. Google it. Put it in /lib folder.");
                }
            }
        }
        this.status = Status.NOT_READY;
        return null;
    }

    @Override
    public void executeUpdate(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sql);
                int index = 1;
                for (Object param : parameters) {
                    ps.setObject(index, param);
                    index++;
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connector.disconnector(conn, ps);
        }
    }

    @Override
    public Table executeQuery(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement ps = null;
        List<Column> columns = new ArrayList<>();
        List<Row> rows = new ArrayList<>();
        try {
            conn = getSQLConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sql);
                int index = 1;
                for (Object param : parameters) {
                    ps.setObject(index, param);
                    index++;
                }
                ResultSet rs = ps.executeQuery();
                int colCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= colCount; i++)
                    columns.add(new CreateColumn(
                            rs.getMetaData().getColumnName(i),
                            Datatype.OBJECT
                    ));
                while (rs.next()) {
                    List<Cell> cells = new ArrayList<>();
                    for (Column col : columns) {
                        col.addCell(new CreateCell(
                                Datatype.OBJECT,
                                rs.getObject(col.getName()),
                                col,
                                false,
                                false
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connector.disconnector(conn, ps);
        }

        return new CreateTable(
                "Result set",
                columns.toArray(new Column[0])
        )/*.setRows(rows.toArray(new Row[0]))*/;
    }

    @Override
    public void writeToDatabase(Entity... entities) throws TableUnassignedException, EntityNotUniqueException {
        for (Entity entity : entities) {
            System.out.println(entity.getEntityType());
            switch (entity.getEntityType()) {
                case TABLE -> {
                    Table table = (Table) entity;
                    //check if the table has a primary column:
                    PrimaryColumn priColumn = table.getPrimaryColumn();

                    //this list will contain the columns being added in query form e.g. name VARCHAR(100) UNIQUE,
                    List<StringBuilder> columnTemps = new ArrayList<>();
                    if (priColumn != null) {
                        if (!priColumn.isPrimary())
                            throw new IllegalArgumentException("The column: " + priColumn.getName() + " is not Unique");
                        //get primary column:
                        StringBuilder pc = new StringBuilder(priColumn.getName() + " " + Datatype.toString(priColumn.getDatatype()));
                        if (priColumn.isNotNull())
                            pc.append(" NOT NULL");
                        columnTemps.add(pc);
                    }
                    //add the remaining columns:
                    List<Column> columns = table.getColumns();
                    columns.remove(priColumn);
                    for (Column col : columns) {
                        StringBuilder nc = new StringBuilder();
                        nc.append(", ").append(col.getName()).append(" ").append(Datatype.toString(col.getDatatype()));
                        if (col.isNotNull())
                            nc.append(" NOT NULL");
                        columnTemps.add(nc);
                    }
                    //add constraints:
                    columns = table.getUniqueColumns();
                    columns.remove(priColumn);
                    for (Column col : columns)
                        columnTemps.add(new StringBuilder(", UNIQUE (" + col.getName() + ")"));
                    if (priColumn != null)
                        columnTemps.add(new StringBuilder(", PRIMARY KEY (" + priColumn.getName() + ")"));

                    StringBuilder params = new StringBuilder();
                    for (StringBuilder sb : columnTemps)
                        params.append(sb.toString());
                    String finalParams = params.toString();
                    if (priColumn == null)
                        finalParams = finalParams.substring(2);

                    executeUpdate("CREATE TABLE IF NOT EXISTS " + table.getName() + "(" + finalParams + ")");
                }
                case COLUMN -> {
                    Column column = (Column) entity;
                    if (column.getParentTable() == null)
                        throw new TableUnassignedException("Must assign a table to a column to write it.");
                    String table = column.getParentTable().getName();
                    boolean duplicate = false;
                    for (Column col : new TableByName(this, table).getColumns()) {
                        if (col == null) break;
                        if (Objects.equals(col.getName(), column.getName())) {
                            if (this.debugMode)
                                System.err.println(
                                        "Column duplicate detected. Skipped writing column: " + table + "."
                                                + column.getName()
                                );
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        executeUpdate(
                                "ALTER TABLE " + table + " ADD " + column.getName() + " " +
                                        Datatype.toString(column.getDatatype())
                        );
                        if (column.isUnique())
                            executeUpdate("ALTER TABLE " + table + " ADD UNIQUE (" + column.getName() + ")");
                        if (column.isNotNull())
                            executeUpdate("ALTER TABLE " + table + " MODIFY " + column.getName() + " NOT NULL");
                        if (column.getDefaultValue() != null) {
                            executeUpdate("ALTER TABLE " + table + " ALTER " + column.getName() +
                                    " SET DEFAULT " + column.getDefaultValue());
                        }
                    }
                }
                case ROW -> {
                    Row row = (Row) entity;
                    if (row.getParentTable() == null)
                        throw new TableUnassignedException("Must assign a table to a row to write it.");
                    Table table = new TableByName(this, row.getParentTable().getName());
                    //check for duplicates
                    boolean duplicate = false;
                    for (Column col : table.getUniqueColumns()) {
                        for (Cell c1 : Objects.requireNonNull(col.getCells())) {
                            for (Cell c2 : row.getCells()) {
                                if (Objects.equals(c1.getData(), c2.getData())) {
                                    if (this.debugMode)
                                        System.err.println(
                                                "Row duplicate detected. Skipped writing row with unique cell value: '" +
                                                        c2.getData() + "' in column: " + c2.getColumn().getName()
                                        );
                                    duplicate = true;
                                }
                            }
                        }
                    }
                    if (!duplicate) {
                        StringBuilder columns = new StringBuilder();
                        StringBuilder unknowns = new StringBuilder();
                        List<Object> values = new ArrayList<>();
                        for (Cell cell : row.getCells()) {
                            columns.append(cell.getColumn().getName()).append(", ");
                            unknowns.append("?, ");
                            values.add(cell.getData());
                        }
                        executeUpdate("INSERT INTO " + table.getName() + " (" +
                                columns.substring(0, columns.length() - 2) + ") VALUES (" +
                                unknowns.substring(0, unknowns.length() - 2) + ")", values.toArray(new Object[0]));
                    }
                }
                case CELL -> {
                    Cell cell = (Cell) entity;
                    if (cell.getParentTable() == null)
                        throw new TableUnassignedException("Must assign a table to a row to write it.");
                    if (cell.getRowIdentifier() == null)
                        throw new EntityNotUniqueException("This cell needs a row identifier cell assigned to it.");
                    String table = cell.getParentTable().getName();
                    Cell rowId = cell.getRowIdentifier();
                    executeUpdate(
                            "UPDATE " + table + " SET " + cell.getColumn().getName() + "=? WHERE " +
                                    rowId.getColumn().getName() + "=?",
                            cell.getData(), rowId.getData()
                    );
                }
            }
        }
    }
}
