package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.*;
import java.util.*;

public class TemperatureEffectsDatabase {
    private static final String EFFECTS_FOLDER = "effect";
    private static final File EFFECTS_FILE;
    private static final Gson gson;
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
    private List<TemperatureEffect> coldEffects = null;
    private List<TemperatureEffect> hotEffects = null;

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


    private static void verifyCache() {
        if (get().coldEffects == null || get().hotEffects == null) {
            Collection<TemperatureEffect> temperatureEffects = get().effects.values();
            get().coldEffects = new ArrayList<>() {{
                for (TemperatureEffect temperatureEffect : temperatureEffects) {
                    if (temperatureEffect.getTemperatureStart() < 0) {
                        add(temperatureEffect);
                    }
                }
            }};
            get().coldEffects.sort((o1, o2) -> compareEffects(o2, o1));
            get().hotEffects = new ArrayList<>() {{
                for (TemperatureEffect temperatureEffect : temperatureEffects) {
                    if (temperatureEffect.getTemperatureStart() > 0) {
                        add(temperatureEffect);
                    }
                }
            }};
            get().hotEffects.sort(TemperatureEffectsDatabase::compareEffects);
        }
    }

    public static List<TemperatureEffect> getAllHot() {
        verifyCache();
        return get().hotEffects;
    }

    public static List<TemperatureEffect> getAllCold() {
        verifyCache();
        return get().coldEffects;
    }

    private static int compareEffects(TemperatureEffect o1, TemperatureEffect o2) {
        double difference = o2.getTemperatureStart() - o1.getTemperatureStart();
        if (difference == 0) {
            return o1.getPotionEffect().compareTo(o2.getPotionEffect());
        } else if (difference < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
