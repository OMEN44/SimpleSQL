package com.github.OMEN44.simpleSQL.logger;

import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.HasTable;

@SuppressWarnings("unused")
public class MissingColumnException extends SimpleSQLException {
    public MissingColumnException(String message) {
        super(message);
    }

    public MissingColumnException(String message, HasTable hasTable) {
        super(message, hasTable.getEntityType());
    }

    public MissingColumnException(String message, Entity.InstanceType instanceType) {
        super(message, instanceType);
    }

    public MissingColumnException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingColumnException(Throwable cause) {
        super(cause);
    }
}
