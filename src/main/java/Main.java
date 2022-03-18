import connectors.Connector;
import connectors.InitConnector;
import dbProfiles.MySQL;
import dbProfiles.SQLite;
import entities.cell.BasicCell;
import entities.cell.BasicCellImpl;
import entities.column.Column;
import entities.column.CreateColumn;
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

        Connector conn = new InitConnector(sqLite);

        BasicCell basicCell = new BasicCellImpl(Datatype.INT, 10);

        Column column = new CreateColumn(
                "test",
                Datatype.INT
        );

        column.getParentTable();

    }
}
