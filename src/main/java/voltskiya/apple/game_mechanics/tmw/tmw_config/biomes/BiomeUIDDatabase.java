package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import apple.utilities.util.FileFormatting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.MinecraftKey;
import plugin.util.plugin.plugin.util.plugin.FileIOServiceNow;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.utilities.util.storage.GsonTypeAdapterUtils;

import java.io.File;
import java.util.HashMap;

public class BiomeUIDDatabase implements SaveFileable {
    private static final String FILENAME = FileFormatting.extensionJson("biomeUids");
    private static BiomeUIDDatabase instance;
    private static AppleJsonDatabaseSingleton<BiomeUIDDatabase> databaseManager;
    private transient final HashMap<MinecraftKey, Integer> uids = new HashMap<>();
    private transient final HashMap<Integer, MinecraftKey> uids2 = new HashMap<>();
    private int currentBiomeUid = 0;

    public BiomeUIDDatabase() {
        instance = this;
    }

    public static BiomeUIDDatabase get() {
        return instance;
    }

    public static void load() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        GsonTypeAdapterUtils.registerMinecraftKeyTypeAdapter(gsonBuilder);
        Gson gson = gsonBuilder.create();
        File folder = PluginTMW.get().getFile("biomes");
        databaseManager = new AppleJsonDatabaseSingleton<>(folder, FileIOServiceNow.get(), gson);
        databaseManager.loadNow(BiomeUIDDatabase.class, FILENAME);
        if (instance == null) instance = new BiomeUIDDatabase();
    }

    public synchronized int getBiome(MinecraftKey biome) {
        Integer biomeUid = uids.get(biome);
        if (biomeUid == null) {
            biomeUid = getCurrentBiomeUid();
            uids.put(biome, biomeUid);
            uids2.put(biomeUid, biome);
            save();
        }
        return biomeUid;
    }

    private void save() {
        databaseManager.save(this);
    }

    public synchronized MinecraftKey getBiome(int biomeUid) {
        return uids2.get(biomeUid);
    }

    private int getCurrentBiomeUid() {
        return currentBiomeUid++;
    }

    @Override
    public String getSaveFileName() {
        return FILENAME;
    }
}
