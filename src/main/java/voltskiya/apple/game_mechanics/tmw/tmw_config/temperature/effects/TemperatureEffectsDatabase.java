package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TemperatureEffectsDatabase {
    private static final String EFFECTS_FOLDER = "effect";
    private static final File EFFECTS_FILE;
    private static Gson gson;
    private static TemperatureEffectsDatabase instance;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        // get the effects from our db
        File effectsFolder = new File(PluginTMW.get().getDataFolder(), EFFECTS_FOLDER);
        effectsFolder.mkdirs();
        EFFECTS_FILE = new File(effectsFolder, "effectsDB.json");
        try {
            if (EFFECTS_FILE.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(EFFECTS_FILE))) {
                    instance = gson.fromJson(reader, TemperatureEffectsDatabase.class);
                }
            } else {
                EFFECTS_FILE.createNewFile();
                instance = new TemperatureEffectsDatabase();
                save();
            }
        } catch (IOException e) {
            instance = null;
            e.printStackTrace();
        }
    }

    private final HashMap<UUID, TemperatureEffect> effects = new HashMap<>();

    public synchronized static void addEffect(TemperatureEffect effect) {
        get().effects.put(effect.getUUID(), effect);
        save();
    }

    public static TemperatureEffectsDatabase get() {
        return instance;
    }

    private static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EFFECTS_FILE))) {
            gson.toJson(get(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<TemperatureEffect> getAll() {
        return new ArrayList<>(get().effects.values());
    }
}
