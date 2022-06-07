package simpleSQL.logger;

import java.sql.SQLException;

public class Logger {

    public static boolean debugMode;

    public static void printSQLException(SQLException ex) throws SimpleSQLException {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                System.err.println("SQLState: " + ((SQLException) e).getSQLState() + "\n");
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode() + "\n");
                System.err.println("Message: " + e.getMessage() + "\n");
                System.err.println("The database is not connected! please ensure that the login credentials are " +
                        "correct and the database is running!\n");
            }
        }
        ex.printStackTrace();
        throw new SimpleSQLException("Database not connected.");
    }

    public static void debug(String message, Boolean... strong) {
        if (isDebugMode()) {
            if (strong.length >= 1) {
                if (strong[0])
                    System.err.println("[SSQL-ERROR]: " + message);
                else
                    System.out.println("[SSQL-DEBUG]: " + message);
            } else System.out.println("[SSQL-DEBUG]: " + message);
        }
    }

    public static void debugMode(boolean b) {
        debugMode = b;
        log("Debug mode enabled");
    }

    public static void debugMode(boolean b, boolean silent) {
        debugMode = b;
        if (!silent)
            log("Debug mode enabled");
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void log(Object message) {
        System.out.println("[SSQL-STATE]: " + message);
    }

    /**
     * @param message Message to be logged
     * @param logLevel warning level colour, the higher, the more urgent
     */
    public static void log(Object message, int logLevel) {
        switch (logLevel) {
            case 1 -> System.out.println(ANSI_GREEN + "[SSQL-STATE]: " + message + ANSI_RESET);
            case 2 -> System.out.println(ANSI_BLUE + "[SSQL-STATE]: " + message + ANSI_RESET);
            case 3 -> System.out.println(ANSI_CYAN + "[SSQL-STATE]: " + message + ANSI_RESET);
            case 4 -> System.out.println(ANSI_YELLOW + "[SSQL-STATE]: " + message + ANSI_RESET);
            case 5 -> System.out.println(ANSI_RED + "[SSQL-STATE]: " + message + ANSI_RESET);
        }
    }

    public static void error(Object message) {
        System.err.println("[SSQL-ERROR]: " + message);
    }
}
