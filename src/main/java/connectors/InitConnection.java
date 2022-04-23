package connectors;

import connectors.dbProfiles.Database;
import connectors.dbProfiles.MySQL;
import connectors.dbProfiles.SQLite;
import entities.*;
import impl.*;
import logger.Logger;
import logger.TableUnassignedException;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class InitConnection implements Connector {

    public Connection connection;
    private final Database DATABASE;
    private final Database.DatabaseType connType;
    private Status status;

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
                    System.out.println(2);
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
    public Table executeQuery(String sqlStatement, Object... parameters) {
        Connection conn = null;
        PreparedStatement ps = null;
        List<Column> columns = new ArrayList<>();
        List<Row> rows = new ArrayList<>();
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
                int colCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= colCount; i++)
                    columns.add(new CreateColumn(
                            rs.getMetaData().getColumnName(i),
                            Datatype.OBJECT
                    ));
                int i = 1;
                while (rs.next()) {
                    //System.out.println(i);
                    List<Cell> cells = new ArrayList<>();
                    for (Column col : columns)
                        cells.add(new CreateCell(
                                Datatype.OBJECT,
                                rs.getObject(col.getName()),
                                col,
                                false,
                                false
                        ));
                    rows.add(new CreateRow(
                            cells.toArray(new Cell[0])
                    ));
                    i++;
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
        ).setRows(rows.toArray(new Row[0]));
    }

    @Override
    public void writeToDatabase(Entity entity) throws TableUnassignedException {
        switch (entity.getObjectType()) {
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
                    StringBuilder pc = new StringBuilder(priColumn.getName() + " " + priColumn.getDatatype());
                    if (priColumn.isNotNull())
                        pc.append(" NOT NULL");
                    columnTemps.add(pc);
                }
                //add the remaining columns:
                List<Column> columns = table.getColumns();
                columns.remove(priColumn);
                for (Column col : columns) {
                    StringBuilder nc = new StringBuilder();
                    nc.append(", ").append(col.getName()).append(" ").append(col.getDatatype());
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
        }
    }
}
