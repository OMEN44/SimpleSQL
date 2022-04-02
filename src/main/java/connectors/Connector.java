package connectors;

import dbProfiles.Database;
import entities.Table;
import logger.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("unused")
public interface Connector {

    enum ConnectionType {
        SQLITE,
        MYSQL
    }

    /**
     * @return Returns the {@link ConnectionType} that is in use with the current connection.
     */
    ConnectionType connectorType();

    /**
     * @return If the SQL connection is currently up or is connecting.
     */
    Status getStatus();

    /**
     * @return The database object that is in use. This can be used to get information like what host the database is running on.
     */
    Database getDatabase();

    /**
     * @return The correct connection for the database being used.
     */
    Connection getSQLConnection();

    /**
     * @apiNote Forces the connection to close.
     * @param conn Connection being closed.
     */
    static void closeConnection(Connection conn) {
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
            if (ps != null)
                ps.close();
            if (conn != null)
                closeConnection(conn);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param sqlStatement Statement to be executed, unknown values are to be represented with a '?'.
     * @param parameters Objects to be inserted into the prepared statement in the order they should appear.
     */
    void executeUpdate(String sqlStatement, Object... parameters);

    Table executeQuery(String sqlStatement, Object... parameters);

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
