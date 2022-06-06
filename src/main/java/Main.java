/*import simpleSQL.connectors.Connector;
import simpleSQL.connectors.InitConnection;
import simpleSQL.connectors.dbProfiles.Database;
import simpleSQL.connectors.dbProfiles.MySQL;
import simpleSQL.connectors.dbProfiles.SQLite;
import simpleSQL.entities.Row;
import simpleSQL.entities.Table;

import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        @SuppressWarnings("unused")
        SQLite sqLite = new SQLite(
                "testing",
                "C:\\Users\\huons\\Documents\\programming\\JavaPrograms\\SimpleSQL\\database"
        );

        @SuppressWarnings("unused")
        MySQL indroCraft = new MySQL(
                3306,
                "Indrocraft",
                "192.168.50.128",
                "omen",
                "Minecraft$44"
        );

        @SuppressWarnings("unused")
        MySQL mySQL = new MySQL(
                3306,
                "pmdb",
                "localhost",
                "root",
                ""
        );

        Connector conn = new InitConnection(sqLite);
        conn.debugMode(true);

        Database inUse = conn.getDatabase();
        List<Table> tables = inUse.getTables();

        //get tableByName ignores primary keys

        *//*Table table = conn.executeQuery("SELECT * FROM pm_marriage");
        for (Row col : table.getRows()) {
            System.out.println(col);
        }*//*

        for (Table tab : tables) {
            if (Objects.equals(tab.getName(), "pm_marriage")) {
                System.out.println("TableByName");
                for (Row col : tab.getRows()) {
                    System.out.println(col);
                }
            }
        }
    }
}*/
