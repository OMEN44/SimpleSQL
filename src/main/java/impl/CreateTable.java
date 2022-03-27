package impl;

import connectors.Connector;
import entities.Column;
import entities.PrimaryKey;
import entities.Row;
import entities.Table;
import logger.EntityNotUniqueException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateTable implements Table {
    private final String NAME;
    private final List<Column> COLUMNS;
    private PrimaryKey PRIMARY_COLUMN;

    public CreateTable(String name, PrimaryKey primaryColumn, Column... columns) throws EntityNotUniqueException {
        this.NAME = name;
        if (primaryColumn.isPrimary())
            this.PRIMARY_COLUMN = primaryColumn;
        else
            throw new EntityNotUniqueException("The primary column specified is not primary.");
        this.COLUMNS = new ArrayList<>(Arrays.asList(columns));
    }

    public CreateTable(String name, Column... columns) {
        this.NAME = name;
        this.COLUMNS = new ArrayList<>(Arrays.asList(columns));
    }

    public Table write(Connector connector) {
        //check if the table has a primary column:
        PrimaryKey priColumn = this.getPrimaryColumn();

        //this list will contain the columns being added in query form e.g. name VARCHAR(100) UNIQUE,
        List<StringBuilder> columnTemps = new ArrayList<>();
        if (priColumn != null) {
            if (!priColumn.isPrimary())
                throw new IllegalArgumentException("The column: " + priColumn.getName() + " is not Unique");
            //get primary column:
            StringBuilder pc = new StringBuilder(priColumn.getName() + " " + priColumn.getDatatype());
            if (priColumn.isNotNull())
                pc.append(" NOT NULL");
            columnTemps.add(pc);
        }
        //add the remaining columns:
        List<Column> columns = this.getColumns();
        columns.remove(priColumn);
        for (Column col : columns) {
            StringBuilder nc = new StringBuilder();
            nc.append(", ").append(col.getName()).append(" ").append(col.getDatatype());
            if (col.isNotNull())
                nc.append(" NOT NULL");
            columnTemps.add(nc);
        }
        //add constraints:
        columns = this.getUniqueColumns();
        columns.remove(priColumn);
        for (Column col : columns)
            columnTemps.add(new StringBuilder(", UNIQUE (" + col.getName() + ")"));
        if (priColumn != null)
            columnTemps.add(new StringBuilder(", PRIMARY KEY (" + priColumn.getName() + ")"));

        StringBuilder params = new StringBuilder();
        for (StringBuilder sb : columnTemps)
            params.append(sb.toString());
        String finalParams = params.toString();
        if (priColumn == null)
            finalParams = finalParams.substring(2);

        System.out.println("CREATE TABLE IF NOT EXISTS " + this.getName() + "(" + finalParams + ")");

        connector.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.getName() + "(" + finalParams + ")");
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
    public PrimaryKey getPrimaryColumn() {
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

    @Override
    public List<Row> getRows() {
        System.err.println("This table object was created and does not contain rows. Use TableByName#getRows");
        return null;
    }
}
