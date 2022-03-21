package impl;

import connectors.Connector;
import entities.Column;
import entities.Table;
import logger.EntityNotUniqueException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateTable implements Table {
    private final String NAME;
    private final Column PRIMARY_COLUMN;
    private final List<Column> COLUMNS;

    public CreateTable(String name, Column primaryColumn, Column... columns) throws EntityNotUniqueException {
        this.NAME = name;
        if (primaryColumn.isPrimary())
            this.PRIMARY_COLUMN = primaryColumn;
        else
            throw new EntityNotUniqueException("The primary column specified is not primary.");
        this.COLUMNS = new ArrayList<>(Arrays.asList(columns));
    }

    public Table write(Connector connector) {
        StringBuilder sb = new StringBuilder();
        //lang=MySQL
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(this.getName()).append(" (")
                .append(this.getPrimaryColumn().getName())
                .append(" ")
                .append(Datatype.dataTypeAsString(this.getPrimaryColumn().getDatatype()));
        if (getPrimaryColumn().isNotNull())
            sb.append(" NOT NULL");
        List<Object> values = new ArrayList<>();
        if (getPrimaryColumn().getDefaultValue() != null) {
            sb.append(" DEFAULT ?");
            values.add(getPrimaryColumn().getDefaultValue());
        }
        for (Column col : this.getColumns()) {
            sb.append(", ").append(col.getName()).append(" ").append(Datatype.dataTypeAsString(col.getDatatype()));
            if (col.isNotNull())
                sb.append(" NOT NULL");
            if (col.getDefaultValue() != null) {
                sb.append(" DEFAULT ?");
                values.add(col.getDefaultValue());
            }
        }
        Object[] val = values.toArray(new Object[0]);
        for (Object obj : val)
            System.out.println(obj);
        sb.append(", PRIMARY KEY (").append(this.getPrimaryColumn().getName()).append("))");
        connector.executeUpdate(sb.toString(), val);
        System.out.println(sb);
        return this;
    }

    @Override
    public instanceType getObjectType() {
        return instanceType.TABLE;
    }

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public Column getPrimaryColumn() {
        return this.PRIMARY_COLUMN;
    }

    @Override
    public List<Column> getUniqueColumns() {
        List<Column> columns = new ArrayList<>();
        for (Column col : this.COLUMNS) {
            if (col.isUnique()) {
                columns.add(col);
            }
        }
        return columns;
    }

    @Override
    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<>(this.COLUMNS);
        columns.add(this.PRIMARY_COLUMN);
        return columns;
    }
}
