package com.github.OMEN44.simpleSQL.connectors;

import com.github.OMEN44.simpleSQL.connectors.dbProfiles.Database;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.MySQL;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.SQLite;
import com.github.OMEN44.simpleSQL.entities.FromDatabase;
import com.github.OMEN44.simpleSQL.entities.cell.Cell;
import com.github.OMEN44.simpleSQL.entities.cell.CreateCell;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.column.CreateColumn;
import com.github.OMEN44.simpleSQL.entities.row.Row;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.logger.Logger;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;
import com.github.OMEN44.simpleSQL.logger.SimpleSQLException;
import org.intellij.lang.annotations.Language;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.github.OMEN44.simpleSQL.logger.Logger.*;

@SuppressWarnings("unused")
public class Connector implements FromDatabase {

    private final Database database;
    private final Database.DatabaseType connType;
    private Status status;
    public Connection connection;

    public Connector(Database database) {
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
    }

    public boolean test() {
        try {
            getSQLConnection();
            return this.status != Status.FAILED_TEST;
        } catch (SimpleSQLException e) {
            this.status = Status.FAILED_TEST;
            return false;
        }
    }

    @SuppressWarnings("all")
    private void createDBFile(SQLite sqLite) {
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
                error("File write error: " + sqLite.getName() + ".db");
            }
        }
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
            tableList = executeQuery("SELECT name FROM sqlite_master WHERE type=? ORDER BY name", "table")
                    .getColumns().get(0).getCells();
        }
        if (toggleDebug)
            Logger.debugMode(true, true);
        if (tableList.size() == 0)
            debug("No tables found", true);

        //get Tables
        for (Cell cell : tableList)
            tables.add(Table.getTableByName(this, (String) cell.getData()));

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
                    this.status = Status.FAILED_TEST;
                    Logger.error("Test failed for database '" + this.database.getName() +
                            "' please check the database credentials used.");
                    Logger.error("Cause: " + e.getCause());
                    return null;
                }
                this.status = Status.CONNECTED;
                return connection;
            }
            case SQLITE -> {
                SQLite sqLite = (SQLite) this.database;
                this.status = Status.CONNECTING;
                createDBFile(sqLite);
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
                    error("SQLite exception on initialize");
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    error("You need the SQLite JDBC library. Google it. Put it in /lib folder.");
                }
            }
        }
        this.status = Status.NOT_READY;
        return null;
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
                conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param sql Statement to be executed, unknown values are to be represented with a '?'.
     * @param parameters Objects to be inserted into the prepared statement in the order they should appear.
     */
    public void executeUpdate(@Language("SQL") String sql, Object... parameters) {
        if (this.status != Status.FAILED_TEST) {
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
                this.status = Status.FAILED_TEST;
                Logger.error("Test failed for database '" + this.database.getName() +
                        "' please check the database credentials used.");
                Logger.error("Cause: " + e.getCause());
            } finally {
                disconnector(conn, ps);
            }
        } else Logger.error("Update canceled due to failed test.");
    }

    /**
     * @param sql Statement to be executed, unknown values are to be represented with a '?'.
     * @param parameters Objects to be inserted into the prepared statement in the order they should appear.
     * @return Returns the results in the form of a {@link ResultTable}.
     */
    public ResultTable executeQuery(@Language("SQL") String sql, Object... parameters) {
        if (this.status != Status.FAILED_TEST) {
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
                this.status = Status.FAILED_TEST;
                Logger.error("Test failed for database '" + this.database.getName() +
                        "'. Please check the database credentials used.");
                Logger.error("Cause: " + e.getCause());
                return null;
            } finally {
                disconnector(conn, ps);
            }
            debug("Response tabulated");
            System.out.println(this.status);
            debug("Retrieved: " + columns.size() + " columns with, " +
                    Objects.requireNonNull(columns.get(0).getCells()).size() + " rows");
            return new ResultTable(
                    sql,
                    columns.toArray(new Column[0])
            );
        } else Logger.error("Query canceled due to failed test. Returning empty table...");
        return new ResultTable(sql);
    }

    enum Status {
        NOT_READY,
        READY,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
        FAILED_TEST;

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
