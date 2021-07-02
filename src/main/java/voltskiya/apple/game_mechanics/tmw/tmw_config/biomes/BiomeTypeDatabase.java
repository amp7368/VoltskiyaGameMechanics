package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BiomeTypeDatabase {
    private static final Gson gson;
    private final HashMap<String, BiomeType> biomes = new HashMap<>();

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(MobType.class, new MobType.MobTypeSerializer());
        gsonBuilder.registerTypeAdapter(MobType.class, new MobType.MobTypeDeSerializer());
        gson = gsonBuilder.create();

        // get the biomes from our db
        File biomesFolder = new File(PluginTMW.get().getDataFolder(), BIOMES_FOLDER);
        biomesFolder.mkdirs();
        biomesFile = new File(biomesFolder, "biomesDB.json");
        try {
            if (biomesFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(biomesFile))) {
                    instance = gson.fromJson(reader, BiomeTypeDatabase.class);
                }
            } else {
                biomesFile.createNewFile();
                instance = new BiomeTypeDatabase();
                save();
            }
            for (BiomeType biome : instance.biomes.values()) {
                instance.currentBiomeUid = Math.max(instance.currentBiomeUid, biome.getUid());
            }
            for (BiomeType biome : instance.biomes.values()) {
                biome.validateUid();
            }
        } catch (IOException e) {
            instance = null;
            e.printStackTrace();
        }
    }

    private static final String BIOMES_FOLDER = "biomes";
    private static BiomeTypeDatabase instance;

    private static final File biomesFile;

    private int currentBiomeUid = 1;

    public static BiomeTypeDatabase get() {
        return instance;
    }

    public synchronized static void addBiome(BiomeType biome) {
        get().biomes.put(biome.getName(), biome);
        save();
    }

    private static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(biomesFile))) {
            gson.toJson(get(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<BiomeType> getAll() {
        return new ArrayList<>(get().biomes.values());
    }

    public static void removeBiome(BiomeType.BiomeTypeBuilder biome) {
        final String key = biome.getName();
        if (key != null)
            get().biomes.remove(key);
    }

    public static int getCurrentBiomeUid() {
        return get().currentBiomeUid++;
    }

    @Nullable
    public static BiomeType get(int currentBiomeUid) {
        for (BiomeType biome : getAll()) {
            if (biome.getUid() == currentBiomeUid) {
                return biome;
            }
        }
        return null;
    }
}
