package com.github.OMEN44.simpleSQL.logger;

import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.HasTable;

@SuppressWarnings("unused")
public class TableUnassignedException extends SimpleSQLException {
    public TableUnassignedException(String message) {
        super(message);
    }

    public TableUnassignedException(String message, HasTable hasTable) {
        super(message, hasTable.getEntityType());
    }

    public TableUnassignedException(String message, Entity.InstanceType instanceType) {
        super(message, instanceType);
    }

    public TableUnassignedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableUnassignedException(Throwable cause) {
        super(cause);
    }
}
