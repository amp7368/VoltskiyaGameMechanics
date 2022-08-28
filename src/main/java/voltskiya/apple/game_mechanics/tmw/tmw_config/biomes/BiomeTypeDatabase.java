package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import apple.lib.pmc.FileIOServiceNow;
import apple.mc.utilities.data.serialize.GsonSerializeMC;
import apple.utilities.database.SaveFileable;
import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDInst;
import apple.utilities.threading.service.queue.AsyncTaskQueue;
import apple.utilities.util.FileFormatting;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;

public class BiomeTypeDatabase implements SaveFileable {

    private static AppleAJDInst<BiomeTypeDatabase, AsyncTaskQueue> manager;
    private static int currentBiomeUid = 1;
    private final HashMap<String, BiomeType> biomesByName = new HashMap<>();
    private final HashMap<ResourceLocation, String> biomeKeyToName = new HashMap<>();

    public static void load() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(MobType.class, MobType.MobTypeSerializer.get());
        GsonSerializeMC.registerMinecraftKeyTypeAdapter(gsonBuilder);
        File folder = PluginTMW.get().getFile("biomes", "BiomesDB.json");
        manager = AppleAJD.createInst(BiomeTypeDatabase.class, folder,
            FileIOServiceNow.get().taskCreator());
        manager.setSerializingJson(gsonBuilder.create());
        BiomeTypeDatabase instance = manager.loadOrMake();
        for (BiomeType biome : instance.biomesByName.values())
            currentBiomeUid = Math.max(currentBiomeUid, biome.getUid());
        for (BiomeType biome : instance.biomesByName.values())
            biome.validateUid();
    }

    private static String getFileNameStatic() {
        return FileFormatting.extensionJson("biomesDB");
    }

    private static void save() {
        manager.save();
    }

    public synchronized static void addBiome(BiomeType biome) {
        get().biomesByName.put(biome.getName(), biome);
        save();
    }

    public static List<BiomeType> getAll() {
        return new ArrayList<>(get().biomesByName.values());
    }

    public static BiomeTypeDatabase get() {
        return manager.getInstance();
    }

    public static void removeBiome(BiomeType.BiomeTypeBuilder biome) {
        final String key = biome.getName();
        if (key == null) {
            return;
        }
        get().biomesByName.remove(key);
        save();

    }

    public static int getCurrentBiomeUid() {
        return ++currentBiomeUid;
    }

    @Nullable
    public static BiomeType getBiome(ResourceLocation key) {
        String biome = get().biomeKeyToName.get(key);
        return biome == null ? null : get().biomesByName.get(biome);
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

    public void addBiomeMapping(ResourceLocation minecraft, String name) {
        this.biomeKeyToName.put(minecraft, name);
        save();
    }

    public List<ResourceLocation> getMinecraftBiomes(BiomeType.BiomeTypeBuilder biome) {
        final String key = biome.getName();
        if (key == null) {
            return Collections.emptyList();
        }
        List<ResourceLocation> minecraft = new ArrayList<>();
        for (Map.Entry<ResourceLocation, String> biomeKey : biomeKeyToName.entrySet()) {
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

    public void removeMapping(ResourceLocation minecraftBiome) {
        this.biomeKeyToName.remove(minecraftBiome);
        save();
    }
}
