package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeTypeDatabase {
    private static final Map<String, BiomeType> biomes = new HashMap<>();

    static {
        // get the biomes from our db
    }

    public synchronized static void addBiome(BiomeType biome) {
        biomes.put(biome.getName(), biome);
    }

    public static List<BiomeType> getAll() {
        return new ArrayList<>(biomes.values());
    }
}
