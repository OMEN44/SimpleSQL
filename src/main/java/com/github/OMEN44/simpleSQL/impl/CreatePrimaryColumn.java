package com.github.OMEN44.simpleSQL.impl;

import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.entities.PrimaryColumn;

@SuppressWarnings("unused")
public class CreatePrimaryColumn extends CreateColumn implements PrimaryColumn {
    public CreatePrimaryColumn(String name, Datatype dataType, Object defaultValue, boolean notNull) {
        super(name, dataType, defaultValue, notNull, true, true);
    }

    public CreatePrimaryColumn(String name, Datatype dataType) {
        super(name, dataType, null, false, true, true);
    }
}
