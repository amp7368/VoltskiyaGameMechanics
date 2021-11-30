package voltskiya.apple.game_mechanics.decay.config.database;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.decay.PluginDecay;
import voltskiya.apple.game_mechanics.util.FileIOService;

public class DecayBlockDefaultsDatabase implements SaveFileable {
    private static DecayBlockDefaultsDatabase instance;
    private static AppleJsonDatabaseSingleton<DecayBlockDefaultsDatabase> databaseManager;
    private int defaultResistance = 1;
    private int decayRate = 10;
    private int durability = 10;

    public static void load() {
        databaseManager = new AppleJsonDatabaseSingleton<>(
                PluginDecay.get().getFile("decayBlocks"),
                FileIOService.get()
        );
        @Nullable DecayBlockDefaultsDatabase database = databaseManager.loadNow(DecayBlockDefaultsDatabase.class, getSaveFileNameStatic());
        if (database == null) {
            instance = new DecayBlockDefaultsDatabase();
            save();
        } else {
            instance = database;
        }
    }

    public static DecayBlockDefaultsDatabase get() {
        return instance;
    }

    private static void save() {
        databaseManager.save(instance);
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

    @NotNull
    private static String getSaveFileNameStatic() {
        return "decaySettings.json";
    }

    @Override
    public String getSaveFileName() {
        return getSaveFileNameStatic();
    }
}
