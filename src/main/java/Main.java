import connectors.Connector;
import connectors.InitConnection;
import dbProfiles.MySQL;
import dbProfiles.SQLite;
import internal.CreateColumn;
import internal.CreateTable;
import entities.Table;
import internal.Datatype;
import logger.EntityNotUniqueException;
import logger.TableUnassignedException;

public class Main {
    public static void main(String[] args) throws EntityNotUniqueException, TableUnassignedException {

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

        Connector conn = new InitConnection(sqLite);

        Table table = new CreateTable(
                "main_table",
                new CreateColumn("UUID", Datatype.INT, null, false, false, true),
                new CreateColumn("name", Datatype.VARCHAR, "huon", true, false, false),
                new CreateColumn("days", Datatype.INT)
        ).write(conn);



    }
}
