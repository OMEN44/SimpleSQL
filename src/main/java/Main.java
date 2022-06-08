import simpleSQL.connectors.Connector;
import simpleSQL.connectors.InitConnection;
import simpleSQL.connectors.dbProfiles.MySQL;
import simpleSQL.connectors.dbProfiles.SQLite;
import simpleSQL.entities.Table;
import simpleSQL.impl.TableByName;
import simpleSQL.logger.Logger;
import simpleSQL.logger.MissingColumnException;

public class Main {

    public static void main(String[] args) throws MissingColumnException {

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

        Connector conn = new InitConnection(mySQL);
        Logger.debugMode(true);

        //get tableByName ignores primary keys

        Table table = new TableByName(conn, "pm_marriage");
        System.out.println(table.getRows().get(0));
        System.out.println(table);



    }
}
