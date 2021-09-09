package voltskiya.apple.game_mechanics.decay.config.block;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import apple.utilities.util.FileFormatting;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.decay.PluginDecay;
import voltskiya.apple.game_mechanics.util.FileIOService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DecayBlockDatabase implements SaveFileable {
    private static DecayBlockDatabase instance;
    private static AppleJsonDatabaseSingleton<DecayBlockDatabase> databaseManager;
    private transient final HashMap<Material, DecayBlockTemplate> allBlocks = new HashMap<>();
    private final HashMap<Material, DecayBlockTemplate> blocks = new HashMap<>();
    private int defaultResistance = 1;
    private int decayRate = 10;
    private int durability = 10;

    public static void load() {
        databaseManager = new AppleJsonDatabaseSingleton<>(
                FileFormatting.fileWithChildren(PluginDecay.get().getDataFolder(), "decayBlocks"),
                FileIOService.get(),
                new GsonBuilder()
                        .registerTypeAdapter(DecayBlockTemplateRequiredTypeJoined.class, DecayBlockTemplateRequiredTypeJoined.getThisDeserializer())
                        .registerTypeAdapter(DecayBlockTemplateRequiredTypeJoined.class, DecayBlockTemplateRequiredTypeJoined.getThisSerializer())
                        .create()
        );
        @NotNull Collection<DecayBlockDatabase> database = databaseManager.loadAllNow(DecayBlockDatabase.class);
        if (database.isEmpty()) {
            instance = new DecayBlockDatabase();
        } else {
            instance = database.stream().findFirst().get();
        }
    }

    public static DecayBlockDatabase get() {
        return instance;
    }

    public synchronized static void addBlock(DecayBlockTemplate block) {
        instance.blocks.put(block.getIcon(), block);
        for (Material material : block.getMaterials()) {
            instance.allBlocks.put(material, block);
        }
        save();
    }

    public static List<DecayBlockTemplate> getAll() {
        return new ArrayList<>(get().blocks.values());
    }

    private static void save() {
        databaseManager.save(instance);
    }

    @Nullable
    public static DecayBlockTemplate getBlock(Material name) {
        if (name == null || name.isAir()) {
            return null;
        }
        return get().allBlocks.getOrDefault(name, DecayBlockTemplate.DEFAULT_TEMPLATE);
    }

    public static void incrementDecayRate(int i) {
        get().decayRate += i;
        save();
    }

    public static void incrementDurability(int i) {
        get().durability += i;
        save();
    }

    public static void incrementResistance(int i) {
        get().defaultResistance += i;
        save();
    }

    public static int getDecayRate() {
        return get().decayRate;
    }

    public static int getDurability() {
        return get().durability;
    }

    public static int getDefaultResistance() {
        return get().defaultResistance;
    }

    @Override
    public String getSaveFileName() {
        return "decayBlocksDB.json";
    }
}