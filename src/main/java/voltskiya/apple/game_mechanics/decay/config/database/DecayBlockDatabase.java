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
            for (DecayBlockTemplateGrouping grouping : database.blocks.values()) {
                for (DecayBlockTemplate blockTemplate : grouping.getBlocks().values()) {
                    for (MaterialVariant material : blockTemplate.getMaterials().values()) {
                        instance.allBlocks.put(material.material, grouping);
                    }
                }
            }
        }
    }

    public static DecayBlockDatabase get() {
        return instance;
    }

    public synchronized static void addBlock(DecayBlockTemplateGrouping grouping) {
        DecayBlockTemplateGrouping oldGrouping = instance.blocks.put(grouping.getIcon().getMaterial(), grouping);
        if (oldGrouping != null) oldGrouping.setDeleted(true);
        grouping.setDeleted(false);
        for (DecayBlockTemplate blockTemplate : grouping.getBlocks().values()) {
            for (MaterialVariant material : blockTemplate.getMaterials().values()) {
                instance.allBlocks.put(material.material, grouping);
            }
        }
        save();
    }

    public static List<DecayBlockTemplateGrouping> getAll() {
        return new ArrayList<>(get().blocks.values());
    }

    private static void save() {
        databaseManager.save(instance);
    }

    @Nullable
    public static DecayBlockTemplate getBlock(Material material) {
        DecayBlockTemplateGrouping grouping = getGroup(material);
        return getBlock(grouping, material);
    }

    @Nullable
    public static DecayBlockTemplate getBlock(DecayBlockTemplateGrouping grouping, Material material) {
        if (material == null || material.isAir()) return null;
        DecayBlockTemplate block = grouping == null ? null : grouping.getBlock(material);
        if (block == null) {
            return DecayBlockTemplate.defaultWithMaterial(material);
        }
        return block;
    }

    @Nullable
    public static DecayBlockTemplateGrouping getGroup(Material material) {
        if (material == null || material.isAir()) return null;
        return get().allBlocks.get(material);
    }

    @Nullable
    public static MaterialVariant getMaterialVariant(Material material) {
        @Nullable DecayBlockTemplate block = getBlock(material);
        return block == null ? null : block.getMaterial(material);

    }

    @Override
    public String getSaveFileName() {
        return getSaveFileNameStatic();
    }

    @NotNull
    private static String getSaveFileNameStatic() {
        return "decayBlocksDB.json";
    }
}