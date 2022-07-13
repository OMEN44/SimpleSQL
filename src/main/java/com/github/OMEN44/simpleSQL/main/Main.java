package com.github.OMEN44.simpleSQL.main;

import com.github.OMEN44.simpleSQL.connectors.Condition;
import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.MySQL;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.SQLite;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.entities.table.TableFromDatabase;
import com.github.OMEN44.simpleSQL.logger.IllegalConditionException;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;

public class Main {
    public static void main(String[] args) throws MissingColumnException, IllegalConditionException {
        SQLite sqLite = new SQLite(
                "testing",
                "C:\\Users\\huons\\Documents\\Programming\\JavaPrograms\\SimpleSQL\\database\\"
        );

        @SuppressWarnings("unused")
        MySQL mySQL = new MySQL(
                3306,
                "pmdb",
                "localhost",
                "root",
                ""
        );

        Connector connector = new Connector(mySQL).test();
        TableFromDatabase table = Table.getTableByName(connector, "albums");

        table.delete(new Condition(Condition.Type.PLAIN, "Title", "Monteverdi: L'Orfeo"),
                new Condition(Condition.Type.AND, "AlbumId", 345));
        System.out.println(connector.executeQuery("SELECT * From toot;"));

    }
}
