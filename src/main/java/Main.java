import connectors.Connector;
import connectors.InitConnection;
import connectors.dbProfiles.MySQL;
import connectors.dbProfiles.SQLite;
import entities.*;
import impl.*;
import connectors.Datatype;
import logger.EntityNotUniqueException;
import logger.MissingColumnException;
import logger.TableUnassignedException;

import java.util.Objects;

public class Main {

    public static void main(String[] args) throws EntityNotUniqueException, TableUnassignedException, MissingColumnException {

        SQLite sqLite = new SQLite(
                "testing",
                "C:\\Users\\huons\\Documents\\JavaPrograms\\SimpleSQL\\database"
        );

        MySQL indroCraft = new MySQL(
                3306,
                "Indrocraft",
                "192.168.50.128",
                "omen",
                "Minecraft$44"
        );

        MySQL mySQL = new MySQL(
                3306,
                "test",
                "localhost",
                "root",
                ""
        );

        Connector conn = new InitConnection(sqLite);
        conn.debugMode(true);

        Table testGetter = new TableByName(conn, "Schools");
        for (Column col : testGetter.getColumns()) {
            System.out.print(col.getName() + " (");
            System.out.print(col.isPrimary() + "):");
            System.out.println();
            for (Cell cel : Objects.requireNonNull(col.getCells())) {
                System.out.println(cel);
            }
        }

        Column schoolName = new CreatePrimaryColumn("schoolName", Datatype.VARCHAR);
        Column location = new CreateColumn("location", Datatype.VARCHAR, "", false, true, false);

        Table table = new CreateTable(
                "Schools",
                schoolName,
                location
        );

        Column column = (Column) new CreateColumn("Students", Datatype.INT).setParentTable(table);

        Row row = (Row) new CreateRow(
                new CreateCell(Datatype.VARCHAR, "Hell", location),
                new CreateCell(Datatype.INT, "666", column)
        ).setParentTable(table);

        Cell cell = (Cell) new CreateCell(Datatype.VARCHAR, "Bad School", schoolName).setParentTable(table);

        conn.writeToDatabase(table, column, row, cell.setRowIdentifier(new CreateCell(Datatype.VARCHAR, "Hell", location)));

        /*Cell cell = new TableByName(conn, "Schools").getColumns().get(1).getCells().get(0);
        System.out.println(cell.getFullColumn(conn));
        System.out.println(cell.getColumn());*/
    }
}
