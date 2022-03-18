package logger;

import entities.Entity;
import entities.HasTable;

@SuppressWarnings("unused")
public class EntityNotUniqueException extends SimpleSQLException {
    public EntityNotUniqueException(String message) {
        super(message);
    }

    public EntityNotUniqueException(String message, HasTable hasTable) {
        super(message, hasTable.getObjectType());
    }

    public EntityNotUniqueException(String message, Entity.instanceType instanceType) {
        super(message, instanceType);
    }

    public EntityNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotUniqueException(Throwable cause) {
        super(cause);
    }
}
