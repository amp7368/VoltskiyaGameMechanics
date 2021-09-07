package voltskiya.apple.game_mechanics.tmw.sql;

import apple.utilities.util.FileFormatting;
import com.google.gson.Gson;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.*;

public class TmwDatabaseConfig {
    private static TmwDatabaseConfig instance;
    public String password = "pass";
    public String username = "user";
    public String url = "url";

    public TmwDatabaseConfig() {
        instance = this;
    }

    public static TmwDatabaseConfig get() {
        return instance;
    }

    public static TmwDatabaseConfig load() {
        File file = FileFormatting.fileWithChildren(PluginTMW.get().getDataFolder(), "tmwDatabaseConfig.json");
        file.getParentFile().mkdirs();
        Gson gson = new Gson();
        TmwDatabaseConfig config;
        try {
            config = gson.fromJson(new BufferedReader(new FileReader(file)), TmwDatabaseConfig.class);
        } catch (FileNotFoundException e) {
            config = new TmwDatabaseConfig();
        }
        try {
            file.createNewFile();
            gson.toJson(config, new BufferedWriter(new FileWriter(file)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return config;
    }
}
