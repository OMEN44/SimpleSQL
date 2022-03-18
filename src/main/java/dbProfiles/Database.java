package dbProfiles;

@SuppressWarnings("unused")
public interface Database {
    enum databaseType {
        MySQL,
        SQLite
    }

    databaseType getDatabaseType();
    String getURL();
    String getName();

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
