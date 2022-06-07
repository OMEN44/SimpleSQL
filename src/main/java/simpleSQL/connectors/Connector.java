package simpleSQL.connectors;

import simpleSQL.connectors.dbProfiles.Database;
import simpleSQL.entities.Entity;
import simpleSQL.entities.Table;
import simpleSQL.logger.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * the {@code Connector} is the actual connection to the database and requires a
 */
@SuppressWarnings("unused")
public interface Connector {

    /**
     * @return Returns the {@link simpleSQL.connectors.dbProfiles.Database.DatabaseType} that is in use with the current connection.
     */
    Database.DatabaseType databaseType();

    /**
     * @return If the SQL connection is currently up or is connecting.
     */
    Status getStatus();

    /**
     * @apiNote UNFINISHED
     * @return The database object that is in use. This can be used to get information like what host the database is running on.
     */
    Database getDatabase() throws MissingColumnException;

    /**
     * @return The correct connection for the database being used.
     */
    Connection getSQLConnection() throws SimpleSQLException;

    /**
     * @apiNote Forces the connection to close.
     * @param conn Connection being closed.
     */
    static void closeConnection(Connection conn) throws SimpleSQLException {
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
     * @param sqlStatement Statement to be executed, unknown values are to be represented with a '?'.
     * @param parameters Objects to be inserted into the prepared statement in the order they should appear.
     */
    void executeUpdate(String sqlStatement, Object... parameters);

    /**
     * @param sqlStatement Statement to be executed, unknown values are to be represented with a '?'.
     * @param parameters Objects to be inserted into the prepared statement in the order they should appear.
     * @return Returns the results in the form of a table.
     * @apiNote This method adds result cells to rows not columns. You must iterate through rows not columns.
     */
    Table executeQuery(String sqlStatement, Object... parameters);

    void writeToDatabase(Entity... entity) throws TableUnassignedException, EntityNotUniqueException, MissingColumnException;

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