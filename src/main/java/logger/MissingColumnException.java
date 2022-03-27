package logger;

import entities.Entity;
import entities.HasTable;

@SuppressWarnings("unused")
public class MissingColumnException extends SimpleSQLException {
    public MissingColumnException(String message) {
        super(message);
    }

    public MissingColumnException(String message, HasTable hasTable) {
        super(message, hasTable.getObjectType());
    }

    public MissingColumnException(String message, Entity.instanceType instanceType) {
        super(message, instanceType);
    }

    public MissingColumnException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingColumnException(Throwable cause) {
        super(cause);
    }
}
