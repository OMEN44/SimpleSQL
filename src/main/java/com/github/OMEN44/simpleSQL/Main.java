package com.github.OMEN44.simpleSQL;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.MySQL;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.SQLite;
import com.github.OMEN44.simpleSQL.entities.column.ColumnByName;
import com.github.OMEN44.simpleSQL.entities.column.ForeignKey;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;

public class Main {
    public static void main(String[] args) throws MissingColumnException {
        SQLite sqLite = new SQLite(
                "testing",
                "C:\\Users\\huons\\Documents\\Programming\\JavaPrograms\\SimpleSQL\\database\\"
        );

        MySQL mySQL = new MySQL(
                3306,
                "testing",
                "localhost",
                "root",
                ""
        );

        Connector connector = new Connector().init(mySQL);
        ForeignKey column = new ColumnByName(connector, "client_id", "payments").toForeignKey();

        System.out.println(column);
        System.out.println("ref location: " + column.getReferencedTableNames() + "." + column.getReferencedColumnName() + "\n");

        for (String table : connector.getDatabase().getTableNames()) {
            ResultTable rt = connector.executeQuery("show create table " + table);
            rt.next(2);
            System.out.println(rt);
            System.out.println();
        }

        /*Connector conn = new Connector(sqLite);
        Table table = new TableByName(conn, "customers");
        for (Column col : table.getColumns()) {
            System.out.println(col.getName());
        }
        System.out.println();
        for (ForeignKey key : table.getForeignKeys()) {
            System.out.println(key.getName());
        }*/
    }
}
