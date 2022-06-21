package com.github.OMEN44.simpleSQL;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.Datatype;
import com.github.OMEN44.simpleSQL.connectors.InitConnection;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.MySQL;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.SQLite;
import com.github.OMEN44.simpleSQL.entities.column.*;
import com.github.OMEN44.simpleSQL.entities.table.CreateTable;
import com.github.OMEN44.simpleSQL.entities.table.ResultTable;
import com.github.OMEN44.simpleSQL.entities.table.Table;
import com.github.OMEN44.simpleSQL.logger.Logger;
import com.github.OMEN44.simpleSQL.logger.MissingColumnException;

public class Main {
    public static void main(String[] args) throws MissingColumnException {
        MySQL mySQL = new MySQL(
                3306,
                "pmdb",
                "localhost",
                "root",
                ""
        );

        MySQL indro = new MySQL(
                3306,
                "indrocraft",
                "omenproject.zapto.org",
                "mysql",
                "Mysql44$"
        );

        SQLite sqLite = new SQLite(
                "testing",
                "C:\\Users\\huons\\Documents\\Programming\\JavaPrograms\\SimpleSQL\\database"
        );
        Logger.debugMode(true);

        //sqlite
        Connector connector = new InitConnection(sqLite);

        ResultTable rt = connector.executeQuery("SELECT * FROM clients");
        while (rt.nextCol()) {
            System.out.println(rt.getCol().getName());
        }

        //mySQL
        Connector connectorSQL = new InitConnection(mySQL);
        Column col = new ColumnByName(connector, "client_id", "clients");

        System.out.println(col.isPrimary());
        System.out.println(col.isUnique());
        System.out.println(col.isForeignKey());
        System.out.println(col.isNotNull());
        System.out.println("'" + col.getDefaultValue() + "'");
        System.out.println(col.getDatatype());

    }
}

