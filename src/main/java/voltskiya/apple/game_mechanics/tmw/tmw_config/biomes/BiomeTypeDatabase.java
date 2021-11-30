package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import apple.utilities.util.FileFormatting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.MinecraftKey;
import org.jetbrains.annotations.Nullable;
import plugin.util.plugin.plugin.util.plugin.FileIOServiceNow;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.utilities.util.storage.GsonTypeAdapterUtils;

import java.io.File;
import java.util.*;

public class BiomeTypeDatabase implements SaveFileable {
    private static AppleJsonDatabaseSingleton<BiomeTypeDatabase> databaseManager;
    private static int currentBiomeUid = 1;
    private final HashMap<String, BiomeType> biomesByName = new HashMap<>();

    private static BiomeTypeDatabase instance;
    private final HashMap<MinecraftKey, String> biomeKeyToName = new HashMap<>();

    public BiomeTypeDatabase() {
        instance = this;
    }

    public static void load() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(MobType.class, MobType.MobTypeSerializer.get());
        GsonTypeAdapterUtils.registerMinecraftKeyTypeAdapter(gsonBuilder);
        Gson gson = gsonBuilder.create();
        File folder = PluginTMW.get().getFile("biomes");
        databaseManager = new AppleJsonDatabaseSingleton<>(folder, FileIOServiceNow.get(), gson);
        databaseManager.loadNow(BiomeTypeDatabase.class, getFileNameStatic());
        if (instance == null) instance = new BiomeTypeDatabase();
        for (BiomeType biome : instance.biomesByName.values())
            currentBiomeUid = Math.max(currentBiomeUid, biome.getUid());
        for (BiomeType biome : instance.biomesByName.values())
            biome.validateUid();
    }

    private static String getFileNameStatic() {
        return FileFormatting.extensionJson("biomesDB");
    }

    private static void save() {
        databaseManager.save(instance);
    }

    public synchronized static void addBiome(BiomeType biome) {
        get().biomesByName.put(biome.getName(), biome);
        save();
    }

    public static List<BiomeType> getAll() {
        return new ArrayList<>(get().biomesByName.values());
    }

    public static BiomeTypeDatabase get() {
        return instance;
    }

    public static void removeBiome(BiomeType.BiomeTypeBuilder biome) {
        final String key = biome.getName();
        if (key == null) return;
        get().biomesByName.remove(key);
        save();

    }

    public static int getCurrentBiomeUid() {
        return ++currentBiomeUid;
    }

    @Nullable
    public static BiomeType getBiome(MinecraftKey key) {
        String biome = instance.biomeKeyToName.get(key);
        return biome == null ? null : instance.biomesByName.get(biome);
    }

    public void addBiomeMapping(MinecraftKey minecraft, String name) {
        this.biomeKeyToName.put(minecraft, name);
        save();
    }

    public List<MinecraftKey> getMinecraftBiomes(BiomeType.BiomeTypeBuilder biome) {
        final String key = biome.getName();
        if (key == null) return Collections.emptyList();
        List<MinecraftKey> minecraft = new ArrayList<>();
        for (Map.Entry<MinecraftKey, String> biomeKey : biomeKeyToName.entrySet()) {
            if (key.equals(biomeKey.getValue())) {
                minecraft.add(biomeKey.getKey());
            }
        }
        return minecraft;
    }

    @Override
    public String getSaveFileName() {
        return getFileNameStatic();
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

    public void removeMapping(MinecraftKey minecraftBiome) {
        this.biomeKeyToName.remove(minecraftBiome);
        save();
    }
}
