package logger;

import java.sql.SQLException;

public class Logger {

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                System.err.println("SQLState: " + ((SQLException) e).getSQLState() + "\n");
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode() + "\n");
                System.err.println("Message: " + e.getMessage() + "\n");
                System.err.println("The database is not connected! please ensure that the login credentials are " +
                        "correct and the database is running!");
            }
        }
        ex.printStackTrace();
    }
}
