package logger;

import connectors.Connector;
import entities.Column;
import entities.Row;
import entities.Table;
import impl.TableByName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TableWriter {
    private Table table;

    public TableWriter(Table table) {
        this.table = table;
    }

    public TableWriter(Connector connector, String name) {
        this.table = new TableByName(connector, name);
    }

    public TableWriter() {

    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void write() {
        List<Column> columns = this.table.getColumns();
        List<Row> rows = this.table.getRows();

        List<String> colNames = new ArrayList<>();
        for (Column col : columns) colNames.add(col.getName());
        
    }
}
