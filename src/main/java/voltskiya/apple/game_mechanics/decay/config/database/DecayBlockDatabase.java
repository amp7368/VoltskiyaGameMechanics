package voltskiya.apple.game_mechanics.decay.config.database;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.decay.PluginDecay;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.MaterialVariant;
import voltskiya.apple.game_mechanics.util.FileIOService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DecayBlockDatabase implements SaveFileable {
    private static DecayBlockDatabase instance;
    private static AppleJsonDatabaseSingleton<DecayBlockDatabase> databaseManager;
    private transient final HashMap<Material, DecayBlockTemplateGrouping> allBlocks = new HashMap<>();
    private final HashMap<Material, DecayBlockTemplateGrouping> blocks = new HashMap<>();

    public static void load() {
        databaseManager = new AppleJsonDatabaseSingleton<>(
                PluginDecay.get().getFile("decayBlocks"),
                FileIOService.get()
        );
        @Nullable DecayBlockDatabase database = databaseManager.loadNow(DecayBlockDatabase.class, getSaveFileNameStatic());
        if (database == null) {
            instance = new DecayBlockDatabase();
            save();
        } else {
            instance = database;
        }
    }

    public static DecayBlockDatabase get() {
        return instance;
    }

    public synchronized static void addBlock(DecayBlockTemplateGrouping grouping) {
        instance.blocks.put(grouping.getIcon().getMaterial(), grouping);
        for (DecayBlockTemplate blockTemplate : grouping.getBlocks().values()) {
            for (MaterialVariant material : blockTemplate.getMaterials().values()) {
                instance.allBlocks.put(material.material, grouping);
            }
        }
        DecayBlockSettingsDatabase.add(grouping.getSettings());
        save();
    }

    public static List<DecayBlockTemplateGrouping> getAll() {
        return new ArrayList<>(get().blocks.values());
    }

    private static void save() {
        databaseManager.save(instance);
    }

    @Nullable
    public static DecayBlockTemplate getBlock(Material name) {
        DecayBlockTemplateGrouping grouping = getGroup(name);
        if (grouping == null) {
            return null;
        } else {
            return grouping.getBlock(name);
        }
    }

    public static DecayBlockTemplateGrouping getGroup(Material name) {
        if (name == null || name.isAir()) {
            return null;
        }
        return get().allBlocks.get(name);
    }

    @NotNull
    private static String getSaveFileNameStatic() {
        return "decayBlocksDB.json";
    }

    @Override
    public String getSaveFileName() {
        return getSaveFileNameStatic();
    }
}