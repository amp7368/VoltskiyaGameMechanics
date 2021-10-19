package voltskiya.apple.game_mechanics.decay.storage;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import apple.utilities.util.FileFormatting;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.decay.PluginDecay;
import voltskiya.apple.game_mechanics.util.FileIOService;

import java.util.Collection;
import java.util.HashMap;

public class MaterialDatabase implements SaveFileable {
    private static final BiMap<Material, Integer> materialToMyUid = HashBiMap.create();
    private static AppleJsonDatabaseSingleton<MaterialDatabase> databaseManager;
    private static int currentUid = 0;
    private static MaterialDatabase instance;
    private final HashMap<Material, Integer> materialToMyUidSaved = new HashMap<>();

    public static void load() {
        databaseManager = new AppleJsonDatabaseSingleton<>(
                FileFormatting.fileWithChildren(PluginDecay.get().getDataFolder(), "shared"),
                FileIOService.get()
        );
        @NotNull Collection<MaterialDatabase> database = databaseManager.loadAllNow(MaterialDatabase.class);
        if (database.isEmpty()) {
            instance = new MaterialDatabase();
            save();
        } else {
            instance = database.stream().findFirst().get();
            materialToMyUid.putAll(instance.materialToMyUidSaved);
            for (Integer i : materialToMyUid.values()) {
                if (i != null && i >= currentUid) currentUid = i + 1;
            }
        }
    }

    private static void save() {
        databaseManager.save(instance);
    }


    /**
     * @param material the material to get the uid of
     * @return the uid corresponding to material or null if material is air
     */
    @NotNull
    public static Integer get(Material material) {
        synchronized (materialToMyUid) {
            if (material == null) material = Material.AIR;
            Integer result = materialToMyUid.get(material);
            if (result != null) return result;
            return put(material);
        }
    }

    @NotNull
    public static Material get(int material) {
        return materialToMyUid.inverse().get(material);
    }

    private static int put(Material material) {
        synchronized (materialToMyUid) {
            int id = currentUid++;
            materialToMyUid.put(material, id);
            instance.materialToMyUidSaved.put(material, id);
            save();
            return id;
        }
    }

    public static String getSaveFileNameStatic() {
        return "materialDB.json";
    }

    @Override
    public String getSaveFileName() {
        return getSaveFileNameStatic();
    }
}
