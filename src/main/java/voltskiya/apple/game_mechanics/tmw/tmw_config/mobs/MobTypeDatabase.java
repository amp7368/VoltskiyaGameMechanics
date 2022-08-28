package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

public class MobTypeDatabase {

    private static final File mobsFile;
    private static MobTypeDatabase instance;

    static {
        // get the mobs from our db
        File mobsFolder = new File(PluginTMW.get().getDataFolder(), PluginTMW.MOBS_FOLDER);
        mobsFolder.mkdirs();
        mobsFile = new File(mobsFolder, "mobsDB.json");
        try {
            if (mobsFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(mobsFile))) {
                    instance = new Gson().fromJson(reader, MobTypeDatabase.class);
                }
            } else {
                mobsFile.createNewFile();
                instance = new MobTypeDatabase();
                save();
            }
        } catch (IOException e) {
            instance = null;
            e.printStackTrace();
        }
    }

    private final Map<String, MobType> mobs = new HashMap<>();

    public static MobTypeDatabase get() {
        return instance;
    }

    public synchronized static void addMob(MobType mob) {
        get().mobs.put(mob.getName(), mob);
        save();
    }

    public static List<MobType> getAll() {
        return new ArrayList<>(get().mobs.values());
    }

    private static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mobsFile))) {
            new Gson().toJson(get(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MobType getMob(String name) {
        return get().mobs.get(name);
    }
}
