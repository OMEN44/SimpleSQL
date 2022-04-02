package dbProfiles;

import entities.Table;

import java.util.List;

@SuppressWarnings("unused")
public interface Database {
    /**
     * This enum is used for identifying what type of database is running and as more are supported it will be updated.
     */
    enum databaseType {
        MySQL,
        SQLite
    }

    /**
     * @return returns the type of database being used.
     */
    databaseType getDatabaseType();

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
     * @return returns the {@link databaseType} of the target database.
     */
    default databaseType getDatabaseType(Database database) {
        if (database instanceof MySQL) {
            return databaseType.MySQL;
        } else if (database instanceof SQLite) {
            return databaseType.SQLite;
        } else {
            return null;
        }
    }
}
