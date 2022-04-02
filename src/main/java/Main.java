import connectors.Connector;
import connectors.InitConnection;
import dbProfiles.MySQL;
import dbProfiles.SQLite;
import entities.Cell;
import entities.Column;
import entities.Row;
import entities.Table;
import impl.CreateCell;
import impl.CreateColumn;
import impl.Datatype;

public class Main {

    public static void main(String[] args) {

        /*SQLite sqLite = new SQLite(
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

        Connector conn = new InitConnection(mySQL);

        Table results = conn.executeQuery("SELECT * FROM players");

        Table results1 = conn.executeQuery("SELECT * FROM hometable");

        for (Row row : results.getRows()) for (Cell cell : row.getCells()) System.out.println(cell.getColumn());

        for (Row row : results1.getRows()) for (Cell cell : row.getCells()) System.out.println(cell.getColumn());*/

        Column column = new CreateColumn(
                "UUID",
                Datatype.VARCHAR
        );

        column.setCells(new CreateCell(
                Datatype.VARCHAR,
                "Hello!",
                column,
                false,
                false
        ),
                new CreateCell(
                        Datatype.VARCHAR,
                        "I am OMEN44",
                        column,
                        false,
                        false
                ),
                new CreateCell(
                        Datatype.VARCHAR,
                        "This is the best party!",
                        column,
                        false,
                        false
                ));

        System.out.println(column);
    }
}
