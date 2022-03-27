package impl;

import entities.PrimaryKey;

public class CreatePrimaryColumn extends CreateColumn implements PrimaryKey {
    public CreatePrimaryColumn(String name, Datatype dataType, Object defaultValue, boolean notNull, boolean isUnique, boolean primaryKey) {
        super(name, dataType, defaultValue, notNull, isUnique, primaryKey);
    }

    public CreatePrimaryColumn(String name, Datatype dataType) {
        super(name, dataType);
    }
}
