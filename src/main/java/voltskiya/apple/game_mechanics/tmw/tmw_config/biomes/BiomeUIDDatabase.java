package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import apple.lib.pmc.FileIOServiceNow;
import apple.mc.utilities.data.serialize.GsonSerializeMC;
import apple.utilities.database.SaveFileable;
import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDInst;
import apple.utilities.threading.service.queue.AsyncTaskQueue;
import apple.utilities.util.FileFormatting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

public class BiomeUIDDatabase implements SaveFileable {

    private static final String FILENAME = FileFormatting.extensionJson("biomeUids");
    private static AppleAJDInst<BiomeUIDDatabase, AsyncTaskQueue> manager;
    private transient final HashMap<ResourceLocation, Integer> uids = new HashMap<>();
    private transient final HashMap<Integer, ResourceLocation> uids2 = new HashMap<>();
    private int currentBiomeUid = 0;


    public static BiomeUIDDatabase get() {
        return manager.getInstance();
    }

    public static void load() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        GsonSerializeMC.registerMinecraftKeyTypeAdapter(gsonBuilder);
        Gson gson = gsonBuilder.create();
        File folder = PluginTMW.get().getFile("biomes", FILENAME);
        manager = AppleAJD.createInst(BiomeUIDDatabase.class, folder,
            FileIOServiceNow.get().taskCreator());
        manager.setSerializingJson(gson);
        manager.loadOrMake();
    }

    public synchronized int getBiome(ResourceLocation biome) {
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
        manager.save();
    }

    public synchronized ResourceLocation getBiome(int biomeUid) {
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
