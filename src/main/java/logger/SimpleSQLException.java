package logger;

import entities.Entity;

@SuppressWarnings("unused")
public class SimpleSQLException extends Exception {
    public SimpleSQLException() {
        super();
    }

    public SimpleSQLException(String message) {
        super(message);
    }

    public SimpleSQLException(String message, Entity.InstanceType instanceType) {
        super(message + " Object missing table is type: " + instanceType.toString());
    }

    public SimpleSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleSQLException(Throwable cause) {
        super(cause);
    }
}
