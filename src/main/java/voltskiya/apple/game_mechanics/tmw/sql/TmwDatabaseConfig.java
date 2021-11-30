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
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            config = gson.fromJson(reader, TmwDatabaseConfig.class);
        } catch (IOException e) {
            config = new TmwDatabaseConfig();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));) {
            if (!file.exists()) file.createNewFile();
            gson.toJson(config, writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return config;
    }
}
