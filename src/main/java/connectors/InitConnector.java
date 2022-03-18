package connectors;

import dbProfiles.Database;
import dbProfiles.MySQL;
import dbProfiles.SQLite;
import logger.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class InitConnector implements Connector {

    public Connection connection;
    private final Database DATABASE;
    private final String HOST;
    private final String PORT;
    private final String NAME;
    private final String USERNAME;
    private final String PASSWORD;
    private final File DATA_FOLDER;
    private final ConnectionType connType;
    private Status status;

    public InitConnector(Database database) {
        switch (database.getDatabaseType()) {
            case MySQL -> {
                MySQL mySQL = (MySQL) database;
                this.PASSWORD = mySQL.getPassword();
                this.USERNAME = mySQL.getUser();
                this.NAME = mySQL.getName();
                this.HOST = mySQL.getHost();
                this.PORT = mySQL.getPort().toString();
                this.DATA_FOLDER = null;
                this.connType = ConnectionType.MYSQL;
            }
            case SQLite -> {
                SQLite sqLite = (SQLite) database;
                this.PASSWORD = null;
                this.USERNAME = null;
                this.NAME = sqLite.getName();
                this.HOST = null;
                this.PORT = null;
                this.DATA_FOLDER = sqLite.getDataFolder();
                this.connType = ConnectionType.SQLITE;
            }
            default -> throw new IllegalArgumentException("Unknown database profile with name: " + database.getName());
        }
        this.DATABASE = database;
        this.status = Status.READY;
    }

    @Override
    public ConnectionType connectorType() {
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
    public Connection getSQLConnection() {
        switch (this.connType) {
            case MYSQL -> {
                this.status = Status.CONNECTING;
                connection = null;
                try {
                    connection = DriverManager.getConnection(
                            "jdbc:mysql://" + this.HOST + ":" + this.PORT + "/" + this.NAME +
                                    "?allowPublicKeyRetrieval=true&useSSL=false",
                            USERNAME,
                            PASSWORD);
                } catch (SQLException e) {
                    System.out.println(2);
                    Logger.printSQLException(e);
                }
                this.status = Status.CONNECTED;
                return connection;
            }
            case SQLITE -> {
                this.status = Status.CONNECTING;
                if (!this.DATA_FOLDER.exists()) {
                    try {
                        this.DATA_FOLDER.mkdir();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                File dataFolder = new File(DATA_FOLDER, this.NAME + ".db");
                if (!dataFolder.exists()) {
                    try {
                        dataFolder.createNewFile();
                    } catch (IOException e) {
                        System.err.println("File write error: " + this.NAME + ".db");
                    }
                }
                try {
                    if (connection != null && !connection.isClosed()) {
                        this.status = Status.CONNECTED;
                        return connection;
                    }
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
                    this.status = Status.CONNECTED;
                    return connection;
                } catch (SQLException ex) {
                    System.err.println("SQLite exception on initialize");
                } catch (ClassNotFoundException ex) {
                    System.err.println("You need the SQLite JDBC library. Google it. Put it in /lib folder.");
                }
            }
        }
        this.status = Status.NOT_READY;
        return null;
    }

    @Override
    public void executeUpdate(String sqlStatement, Object... parameters) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sqlStatement);
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
    public List<Object> executeQuery(String sqlStatement, Object... parameters) {
        Connection conn = null;
        PreparedStatement ps = null;
        List<Object> results = new ArrayList<>();
        try {
            conn = getSQLConnection();
            if (conn != null) {
                ps = conn.prepareStatement(sqlStatement);
                int index = 1;
                for (Object param : parameters) {
                    ps.setObject(index, param);
                    index++;
                }
                ResultSet rs = ps.executeQuery();
                int i = 1;
                while (rs.next()) {
                    System.out.println(i);
                    i++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connector.disconnector(conn, ps);
        }
        return results;
    }
}
