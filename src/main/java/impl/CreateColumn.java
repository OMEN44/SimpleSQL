package impl;

import connectors.Connector;
import entities.*;
import logger.Boxer;
import logger.TableUnassignedException;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class CreateColumn implements Column {
    private final Datatype DATATYPE;
    private final String NAME;
    private final Object DEFAULT_VALUE;
    private final boolean NOT_NULL;
    private final boolean IS_UNIQUE;
    private final boolean PRIMARY_KEY;
    private List<Cell> cells;
    private Table parentTable;

    public CreateColumn(String name, Datatype dataType,
                        Object defaultValue, boolean notNull, boolean isUnique, boolean primaryKey) {
        this.DATATYPE = dataType;
        this.NAME = name;
        this.DEFAULT_VALUE = defaultValue;
        this.NOT_NULL = notNull;
        if (primaryKey)
            this.IS_UNIQUE = true;
        else
            this.IS_UNIQUE = isUnique;
        this.PRIMARY_KEY = primaryKey;
    }

    public CreateColumn(String name, Datatype dataType) {
        this.DATATYPE = dataType;
        this.NAME = name;
        this.DEFAULT_VALUE = null;
        this.NOT_NULL = false;
        this.IS_UNIQUE = false;
        this.PRIMARY_KEY = false;
    }

    public void write(Connector connector) throws TableUnassignedException {
        //check if column has a table:
        Table table = this.getParentTable();
        String query = "ALTER TABLE `" + table.getName() + "` ADD `" + this.getName() +
                "` " + this.getDatatype().toString();
        if (this.NOT_NULL)
            query = query + " NOT NULL";
        else
            query = query + " NULL";

        if (this.PRIMARY_KEY)
            query = query + ", ADD PRIMARY KEY (`" + this.getName() + "`)";
        else if (this.IS_UNIQUE)
            query = query + ", ADD UNIQUE (`" + this.getName() + "`)";

        System.out.println(query);
        connector.executeUpdate(query);
    }

    @Override
    public instanceType getObjectType() {
        return instanceType.COLUMN;
    }

    @Override
    public Table getParentTable() throws TableUnassignedException {
        if (parentTable == null) {
            throw new TableUnassignedException("Parent table has not been set for this object.", this.getObjectType());
        }
        return parentTable;
    }

    @Override
    public HasTable setParentTable(Table table) {
        this.parentTable = table;
        return this;
    }

    @Override
    public List<Cell> getCells() {
        return this.cells;
    }

    @Override
    public Column setCells(Cell... cells) {
        this.cells = Arrays.stream(cells).toList();
        return this;
    }

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public Datatype getDatatype() {
        return this.DATATYPE;
    }

    @Override
    public boolean isNotNull() {
        return this.NOT_NULL;
    }

    @Override
    public Object getDefaultValue() {
        return this.DEFAULT_VALUE;
    }

    @Override
    public boolean isUnique() {
        return this.IS_UNIQUE;
    }

    @Override
    public boolean isPrimary() {
        return this.PRIMARY_KEY;
    }

    @Override
    public String toString() {
        StringBuilder output;
        Boxer boxer = new Boxer();
        try {
            if (this.parentTable != null) boxer.addTitle(getParentTable().getName());
        } catch (TableUnassignedException e) {
            e.printStackTrace();
        }
        if (this.cells == null) {
            boxer.setContent(this.NAME);
            boxer.buildBox();
            output = new StringBuilder(boxer.getOutput());
        } else {
            //get the longest cell length
            int length = this.NAME.length();
            for (Cell cell : this.cells) {
                if (length < cell.getData().toString().length()) length = cell.getData().toString().length();
            }
            System.out.println("length | " + length);

            boxer.setCorner(Boxer.c[7], Boxer.Alignment.BOTTOM_LEFT)
                    .setCorner(Boxer.c[4], Boxer.Alignment.BOTTOM_RIGHT)
                    .setContent(Boxer.addSpace(this.NAME, length))
                    .buildBox();
            output = new StringBuilder(boxer.getOutput());
            for (Cell cell : this.cells) {
                String data = cell.getData().toString();
                int i = data.length();
                data = Boxer.addSpace(data, length);
                System.out.println("actual | " + data.length() + "\n" +
                        "length | " + length);
                boxer = new Boxer(data);
                boxer.buildBox();
                output.append(boxer.getOutput().substring(boxer.getOutput().split("\n")[0].length()));
            }
        }
        return output.toString();
    }
}
