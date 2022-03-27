import connectors.Connector;
import connectors.InitConnection;
import dbProfiles.MySQL;
import dbProfiles.SQLite;
import entities.Column;
import entities.Table;
import impl.*;
import logger.Boxer;
import logger.EntityNotUniqueException;
import logger.MissingColumnException;
import logger.TableUnassignedException;

import java.util.List;

public class Main {

    public static void main(String[] args) throws EntityNotUniqueException, MissingColumnException, TableUnassignedException, Boxer.NoContentException {

        Boxer boxer = new Boxer();
        boxer.setContent("hello I am the best person\nlike ever in the whole world.").enableLineWrap(8).write();

        /*SQLite sqLite = new SQLite(
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

        System.out.println
                (new CreatePrimaryColumn(
                        "UUID", Datatype.INT, null, false, false, true).isPrimary());

        new CreateTable(
                "main_table",
                new CreatePrimaryColumn(
                        "UUID", Datatype.INT, null, false, false, true),
                new CreateColumn(
                        "name", Datatype.VARCHAR, "huon", true, false, false),
                new CreateColumn(
                        "days", Datatype.INT)
        ).write(conn);

        Table table = new CreateTable(
                "players",
                new CreateColumn(
                        "house", Datatype.VARCHAR, null, true, true, false),
                new CreateColumn(
                        "points", Datatype.INT)
        ).write(conn);

        new CreateColumn(
                "number", Datatype.INT, null, false, true, true
        ).setParentTable(table).write(conn);

        Table table1 = new TableByName(conn, "main_table");

        Table table2 = new TableByName(conn, "players");
        List<Column> columns = table2.getColumns();*/
    }
}
