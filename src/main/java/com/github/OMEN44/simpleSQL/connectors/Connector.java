package com.github.OMEN44.simpleSQL.connectors;

import com.github.OMEN44.simpleSQL.connectors.dbProfiles.Database;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.MySQL;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.SQLite;
import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.cell.CreateCell;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.column.CreateColumn;
import com.github.OMEN44.simpleSQL.entities.column.PrimaryKey;
import com.github.OMEN44.simpleSQL.entities.column.UniqueColumn;
import com.github.OMEN44.simpleSQL.entities.row.Row;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.entities.table.TableByName;
import com.github.OMEN44.simpleSQL.logger.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.github.OMEN44.simpleSQL.logger.Logger.debug;
import static com.github.OMEN44.simpleSQL.logger.Logger.log;

@SuppressWarnings("unused")
public class Connector {

    private Database database;
    private Database.DatabaseType connType;
    private Status status;
    public Connection connection;

    public Connector init(Database database) {
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
        this.database = database;
        this.status = Status.READY;
        log("Connector is ready with " + databaseType() + " database for connection");
        return this;
    }

    /**
     * @return Returns the {@link Database.DatabaseType} that is in use with the current connection.
     */
    public Database.DatabaseType databaseType() {
        return this.connType;
    }

    /**
     * @return If the SQL connection is currently up or is connecting.
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * @apiNote UNFINISHED
     * @return The database object that is in use. This can be used to get information like what host the database is running on.
     */
    public Database getDatabase() throws MissingColumnException {
        boolean toggleDebug = false;
        if (Logger.isDebugMode()) {
            Logger.debugMode(false, true);
            toggleDebug = true;
        }
        List<Table> tables = new ArrayList<>();
        List<Cell> tableList = new ArrayList<>();
        if (this.connType == Database.DatabaseType.MYSQL) {
            tableList = executeQuery("SHOW TABLES").getColumns().get(0).getCells();
        } else if (this.connType == Database.DatabaseType.SQLITE) {
            tableList = executeQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")
                    .getColumns().get(0).getCells();
        }
        if (toggleDebug)
            Logger.debugMode(true, true);
        if (tableList.size() == 0)
            debug("No tables found", true);

        //get Tables
        for (Cell cell : tableList)
            tables.add(new TableByName(this, (String) cell.getData()));

        switch (this.database.getDatabaseType()) {
            case SQLITE -> {
                debug("SQLite database retrieved with: " + tables.size() + " tables");
                return new SQLite((SQLite) this.database, tables.toArray(new Table[0]));
            }
            case MYSQL -> {
                debug("MySQL database retrieved with: " + tables.size() + " tables");

                return new MySQL((MySQL) this.database, tables.toArray(new Table[0]));
            }
            default -> {
                debug("No database connected!", true);
                return null;
            }
        }
    }

    /**
     * @return The correct connection for the database being used.
     */
    @SuppressWarnings("all")
    public Connection getSQLConnection() throws SimpleSQLException {
        switch (this.connType) {
            case MYSQL -> {
                MySQL mySQL = (MySQL) this.database;
                this.status = Status.CONNECTING;
                connection = null;
                try {
                    connection = DriverManager.getConnection(
                            this.database.getURL(),
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
                SQLite sqLite = (SQLite) this.database;
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
                    connection = DriverManager.getConnection(this.database.getURL());
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

    /**
     * @apiNote Forces the connection to close.
     * @param conn Connection being closed.
     */
    public static void closeConnection(Connection conn) throws SimpleSQLException {
        try {
            conn.close();
        } catch (SQLException e) {
            Logger.printSQLException(e);
        }
    }

    /**
     * @param conn Connection being closed.
     * @param ps Removes prepared statement.
     */
    static void disconnector(Connection conn, PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
            if (conn != null)
                closeConnection(conn);
        } catch (SQLException | SimpleSQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param sql Statement to be executed, unknown values are to be represented with a '?'.
     * @param parameters Objects to be inserted into the prepared statement in the order they should appear.
     */
    public void executeUpdate(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sql);
                debug("Query prepared for update");
                int index = 1;
                for (Object param : parameters) {
                    ps.setObject(index, param);
                    index++;
                }
                ps.executeUpdate();
                debug("Database updated");
            }
        } catch (SQLException | SimpleSQLException e) {
            e.printStackTrace();
        } finally {
            disconnector(conn, ps);
        }
    }

    /**
     * @param sql Statement to be executed, unknown values are to be represented with a '?'.
     * @param parameters Objects to be inserted into the prepared statement in the order they should appear.
     * @return Returns the results in the form of a {@link ResultTable}.
     */
    public ResultTable executeQuery(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement ps = null;
        List<Column> columns = new ArrayList<>();
        List<Row> rows = new ArrayList<>();
        try {
            conn = getSQLConnection();
            if (conn != null) {
                //prepare statement
                ps = conn.prepareStatement(sql);
                int index = 1;
                //add arguments in their order
                for (Object param : parameters) {
                    ps.setObject(index, param);
                    index++;
                }
                ResultSet rs;
                try {
                    rs = ps.executeQuery();
                } catch (SQLSyntaxErrorException e) {
                    Logger.debug(e.getMessage() + " returning empty table...", true);
                    return new ResultTable(sql);
                }
                debug("Query executed ready to tabulate response");
                //organise results into a Table
                int colCount = rs.getMetaData().getColumnCount();
                //get columns for table
                for (int i = 1; i <= colCount; i++) {
                    columns.add(new CreateColumn(
                            rs.getMetaData().getColumnName(i),
                            Datatype.OBJECT
                    ));
                }
                //add cells
                while (rs.next()) {
                    List<Cell> cells = new ArrayList<>();
                    index = 1;
                    for (Column col : columns) {
                        col.addCell(new CreateCell(
                                Datatype.OBJECT,
                                rs.getObject(index),
                                col,
                                false,
                                false
                        ));
                        index++;
                    }
                }
            }
        } catch (SQLException | SimpleSQLException e) {
            e.printStackTrace();
        } finally {
            disconnector(conn, ps);
        }

        debug("Response tabulated");
        debug("Retrieved: " + columns.size() + " columns with, " +
                Objects.requireNonNull(columns.get(0).getCells()).size() + " rows");
        return new ResultTable(
                sql,
                columns.toArray(new Column[0])
        );
    }

    public void writeToDatabase(Entity... entities) throws TableUnassignedException, MissingColumnException, EntityNotUniqueException {
        for (Entity entity : entities) {
            System.out.println(entity.getEntityType());
            switch (entity.getEntityType()) {
                case TABLE -> {
                    Table table = (Table) entity;
                    //check if the table has a primary column:
                    PrimaryKey priColumn = table.getPrimaryKey();

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
                    List<UniqueColumn> uniqueColumns = table.getUniqueColumns();
                    uniqueColumns.remove(priColumn);
                    for (Column col : uniqueColumns)
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
                            debug("Column duplicate detected. Skipped writing column: " + table + "."
                                    + column.getName(), true);
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
                                    debug("Row duplicate detected. Skipped writing row with unique cell value: '" +
                                            c2.getData() + "' in column: " + c2.getColumn().getName(), true);
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

    enum Status {
        NOT_READY,
        READY,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED;

        private final boolean isInit;

        Status(boolean isInit) {
            this.isInit = isInit;
        }

        Status() {
            this.isInit = false;
        }

        public boolean isInit() {
            return this.isInit;
        }
    }
}
