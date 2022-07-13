package com.github.OMEN44.simpleSQL.logger;

import com.github.OMEN44.simpleSQL.entities.Entity;
import com.github.OMEN44.simpleSQL.entities.HasTable;

@SuppressWarnings("unused")
public class IllegalConditionException  extends SimpleSQLException {
    public IllegalConditionException(String message) {
        super(message);
    }

    public IllegalConditionException(String message, HasTable hasTable) {
        super(message, hasTable.getEntityType());
    }

    public IllegalConditionException(String message, Entity.InstanceType instanceType) {
        super(message, instanceType);
    }

    public IllegalConditionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConditionException(Throwable cause) {
        super(cause);
    }
}
