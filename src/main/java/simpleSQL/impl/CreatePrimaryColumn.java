package simpleSQL.impl;

import simpleSQL.connectors.Datatype;
import simpleSQL.entities.PrimaryColumn;

@SuppressWarnings("unused")
public class CreatePrimaryColumn extends CreateColumn implements PrimaryColumn {
    public CreatePrimaryColumn(String name, Datatype dataType, Object defaultValue, boolean notNull) {
        super(name, dataType, defaultValue, notNull, true, true);
    }

    public CreatePrimaryColumn(String name, Datatype dataType) {
        super(name, dataType, null, false, true, true);
    }
}
