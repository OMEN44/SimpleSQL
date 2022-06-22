package com.github.OMEN44.simpleSQL;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.InitConnection;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.MySQL;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.SQLite;
import com.github.OMEN44.simpleSQL.entities.column.Column;
import com.github.OMEN44.simpleSQL.entities.column.ColumnByName;
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

        //mySQL
        Connector connectorSQL = new InitConnection(mySQL);
        Column col = new ColumnByName(connector, "GenreId", "tracks");

        System.out.println("is primary: " + col.isPrimary());
        System.out.println("is unique: " + col.isUnique());
        System.out.println("is foreign key: " + col.isForeignKey());
        System.out.println("is not null: " + col.isNotNull());
        System.out.println("'" + col.getDefaultValue() + "'");
        System.out.println(col.getDatatype());

    }
}

