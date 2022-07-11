package com.github.OMEN44.simpleSQL;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.MySQL;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.SQLite;
import com.github.OMEN44.simpleSQL.entities.FromDatabase;
import com.github.OMEN44.simpleSQL.entities.column.ColumnByName;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.entities.table.TableFromDatabase;
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

        Connector connector = new Connector(sqLite).test();
        TableFromDatabase table = Table.getTableByName(connector, "albums");

        table.delete(new FromDatabase.Where("Title", "Mozart: Chamber Music"));
        System.out.println(connector.executeQuery("SELECT * FROM albums;"));

    }
}
