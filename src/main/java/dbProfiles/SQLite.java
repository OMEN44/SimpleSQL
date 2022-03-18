package dbProfiles;

import java.io.File;

@SuppressWarnings("unused")
public class SQLite implements Database {
    private final File DATA_FOLDER;
    private final String NAME;

    private String tables;

    public SQLite(String name, File dataFolder) {
        this.NAME = name;
        this.DATA_FOLDER = dataFolder;
    }

    public SQLite(String name, String dataFolderPath) {
        this.NAME = name;
        this.DATA_FOLDER = new File(dataFolderPath);
    }

    public File getDataFolder() {
        return DATA_FOLDER;
    }

    public String getTables() {
        return tables;
    }

    public String getName() {
        return NAME;
    }

    @Override
    public databaseType getDatabaseType() {
        return databaseType.SQLite;
    }

    @Override
    public String getURL() {
        return "jdbc:sqlite:" + this.DATA_FOLDER;
    }
}
