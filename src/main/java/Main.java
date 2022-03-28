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

    public static void main(String[] args) throws Boxer.NoContentException {

        Boxer boxer = new Boxer();
        boxer.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt\n" +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco\n" +
                "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in\n" +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat\n" +
                " non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
                .enableLineWrap(40).write();
        boxer.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt\n" +
                        "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco\n" +
                        "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in\n" +
                        "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat\n" +
                        " non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
                .disableLineWrap().write();

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
