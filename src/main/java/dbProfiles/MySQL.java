package dbProfiles;

import entities.Table;

import java.util.List;

@SuppressWarnings("unused")
public class MySQL implements Database {

    private final String PASSWORD;
    private final String NAME;
    private final String HOST;
    private final String USER;
    private final Integer PORT;

    private List<Table> tables;

    /**
     * @param name     What is the name of the database you want to connect to?
     * @param host     Where is the database being hosted? If it's on this machine set this parameter to localhost.
     * @param port     What port is the server running on, the default port for MySQL is 3306.
     * @param user     What user do you want to access the database as? Should be set to 'root' if possible.
     * @param password What password does that username use? If none just leave as an empty string "".
     */
    public MySQL(Integer port, String name, String host, String user, String password) {
        this.PASSWORD = password;
        this.NAME = name;
        this.HOST = host;
        this.USER = user;
        this.PORT = port;
    }


    /**
     * @return Returns the password being used for the database connection.
     */
    public String getPassword() {
        return PASSWORD;
    }

    /**
     * @return Returns the name of the database being used.
     */
    public String getName() {
        return NAME;
    }

    /**
     * @return Returns the ip of the host machine.
     */
    public String getHost() {
        return HOST;
    }

    /**
     * @return returns the username being used for the connection.
     */
    public String getUser() {
        return USER;
    }

    /**
     * @return returns the port that the database is running on.
     */
    public Integer getPort() {
        return PORT;
    }

    @Override
    public List<Table> getTables() {
        return tables;
    }

    @Override
    public databaseType getDatabaseType() {
        return databaseType.MySQL;
    }

    @Override
    public String getURL() {
        return "jdbc:mysql://" + this.HOST + ":" + this.PORT + "/" + this.NAME +
                "?allowPublicKeyRetrieval=true&useSSL=false";
    }
}
