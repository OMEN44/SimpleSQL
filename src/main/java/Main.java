import connectors.Connector;
import connectors.InitConnection;
import dbProfiles.MySQL;
import dbProfiles.SQLite;
import entities.Column;
import entities.Table;
import impl.*;
import logger.EntityNotUniqueException;

import java.util.List;

public class Main {

    public static void main(String[] args) throws EntityNotUniqueException {
        SQLite sqLite = new SQLite(
                "testing",
                "C:\\Users\\huons\\Documents\\JavaPrograms\\SimpleSQL\\database"
        );

        MySQL mySQL = new MySQL(
                3306,
                "testingdb",
                "localhost",
                "root",
                ""
        );

        Connector conn = new InitConnection(mySQL);

        Table table = new CreateTable(
                "main_table",
                new CreateColumn("UUID", Datatype.INT, null, false, false, true),
                new CreateColumn("name", Datatype.VARCHAR, "huon", true, false, false),
                new CreateColumn("days", Datatype.INT)
        ).write(conn);

        Table table1 = new TableByName(conn, "main_table");
        System.out.println(table1.getPrimaryColumn().getName());

        Table table2 = new TableByName(conn, "players");
        List<Column> columns = table2.getColumns();
        for (Column col : columns) {
            System.out.println(col.getName());
        }

        Column column = new ColumnByName(conn, "ign", table2);
        System.out.println(column.getDatatype());
    }
}
