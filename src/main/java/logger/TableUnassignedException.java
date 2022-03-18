package logger;

import entities.Entity;
import entities.HasTable;

@SuppressWarnings("unused")
public class TableUnassignedException extends SimpleSQLException {
    public TableUnassignedException(String message) {
        super(message);
    }

    public TableUnassignedException(String message, HasTable hasTable) {
        super(message, hasTable.getObjectType());
    }

    public TableUnassignedException(String message, Entity.instanceType instanceType) {
        super(message, instanceType);
    }

    public TableUnassignedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableUnassignedException(Throwable cause) {
        super(cause);
    }
}
