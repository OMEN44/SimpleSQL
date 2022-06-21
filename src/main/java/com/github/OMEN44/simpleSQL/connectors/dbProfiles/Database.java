package com.github.OMEN44.simpleSQL.connectors.dbProfiles;

import com.github.OMEN44.simpleSQL.entities.table.Table;

import java.util.List;

@SuppressWarnings("unused")
public interface Database {
    /**
     * This enum is used for identifying what type of database is running and as more are supported it will be updated.
     */
    enum DatabaseType {
        MYSQL,
        SQLITE
    }

    /**
     * @return returns the type of database being used.
     */
    DatabaseType getDatabaseType();

    /**
     * @return returns the URL being used by jdbc to connect to the database.
     */
    String getURL();

    /**
     * @return returns the name of the database.
     */
    String getName();

    /**
     * @return returns a list of the tables in the database.
     */
    List<Table> getTables();



    /**
     * @param database Database object being tested.
     * @return returns the {@link DatabaseType} of the target database.
     */
    default DatabaseType getDatabaseType(Database database) {
        if (database instanceof MySQL) {
            return DatabaseType.MYSQL;
        } else if (database instanceof SQLite) {
            return DatabaseType.SQLITE;
        } else {
            return null;
        }
    }
}
