package com.github.OMEN44.simpleSQL;

import com.github.OMEN44.simpleSQL.connectors.Connector;
import com.github.OMEN44.simpleSQL.connectors.InitConnection;
import com.github.OMEN44.simpleSQL.connectors.dbProfiles.MySQL;
import com.github.OMEN44.simpleSQL.impl.TableByName;
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

        Connector connector = new InitConnection(mySQL);
        System.out.println(connector.getDatabase().getTables().size());
        System.out.println(new TableByName(
                connector,
                "prime_minister"
        ));
    }
}
