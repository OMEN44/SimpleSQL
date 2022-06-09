package com.github.OMEN44.simpleSQL.logger;

import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.HasTable;

@SuppressWarnings("unused")
public class EntityNotUniqueException extends SimpleSQLException {
    public EntityNotUniqueException(String message) {
        super(message);
    }

    public EntityNotUniqueException(String message, HasTable hasTable) {
        super(message, hasTable.getEntityType());
    }

    public EntityNotUniqueException(String message, Entity.InstanceType instanceType) {
        super(message, instanceType);
    }

    public EntityNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotUniqueException(Throwable cause) {
        super(cause);
    }
}
