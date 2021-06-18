package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import com.google.gson.Gson;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BiomeTypeDatabase {
    private final HashMap<String, BiomeType> biomes = new HashMap<>();

    private static final String BIOMES_FOLDER = "biomes";
    private static BiomeTypeDatabase instance;

    private static final File biomesFile;

    static {
        // get the biomes from our db
        File biomesFolder = new File(PluginTMW.get().getDataFolder(), BIOMES_FOLDER);
        biomesFolder.mkdirs();
        biomesFile = new File(biomesFolder, "biomesDB.json");
        try {
            if (biomesFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(biomesFile))) {
                    instance = new Gson().fromJson(reader, BiomeTypeDatabase.class);
                }
            } else {
                biomesFile.createNewFile();
                instance = new BiomeTypeDatabase();
                save();
            }
        } catch (IOException e) {
            instance = null;
            e.printStackTrace();
        }
    }

    public static BiomeTypeDatabase get() {
        return instance;
    }

    public synchronized static void addBiome(BiomeType biome) {
        get().biomes.put(biome.getName(), biome);
        save();
    }

    private static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(biomesFile))) {
            new Gson().toJson(get(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<BiomeType> getAll() {
        return new ArrayList<>(get().biomes.values());
    }
}
