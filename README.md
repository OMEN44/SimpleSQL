# SimpleSQL
SQL library for using MySQL and SQLite. Uses objects to improve usability instead of strings and SQL statements.

# How to use this Library:

<h3>How to create a connection:<h3/>

```java
import connectors.Connector;
import connectors.InitConnection;
import dbProfiles.MySQL;

public class Main {
    public static void main(String[] args) {
        //MySQL connection:
        MySQL mySQL = new MySQL(
                3306,
                "DatabaseName",
                "localhost",
                "root",
                "password"
        );

        Connector mySqlConn = new InitConnection(mySQL);
        
        //SQLite connection:
        SQLite sqLite = new SQLite(
                "testing",
                "C:\\JavaPrograms\\database"
        );
        
        Connector sqliteConn = new InitConnection(sqLite);
    }
}
```