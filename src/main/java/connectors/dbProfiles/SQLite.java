package connectors.dbProfiles;

import entities.Table;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public class SQLite implements Database {
    private final File DATA_FOLDER;
    private final String NAME;

    private List<Table> tables;

    /**
     * @param name Name of the database.
     * @param dataFolder data folder that the database should be saved to.
     */
    public SQLite(String name, File dataFolder) {
        this.NAME = name;
        this.DATA_FOLDER = dataFolder;
    }

    /**
     * @param name Name of the database.
     * @param dataFolderPath String of the data folder's path.
     */
    public SQLite(String name, String dataFolderPath) {
        this.NAME = name;
        this.DATA_FOLDER = new File(dataFolderPath);
    }

    /**
     * @return The data folder is the location that the database is saved to.
     */
    public File getDataFolder() {
        return DATA_FOLDER;
    }

    @Override
    public List<Table> getTables() {
        return tables;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.SQLITE;
    }

    @Override
    public String getURL() {
        return "jdbc:sqlite:" + this.DATA_FOLDER;
    }
}
