package voltskiya.apple.game_mechanics.decay.config.database;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.decay.PluginDecay;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGroupingSettings;
import voltskiya.apple.game_mechanics.util.FileIOService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DecayBlockSettingsDatabase implements SaveFileable {
    private static DecayBlockSettingsDatabase instance;
    private static AppleJsonDatabaseSingleton<DecayBlockSettingsDatabase> databaseManager;
    private final HashMap<UUID, DecayBlockTemplateGroupingSettings> settings = new HashMap<>();

    public static void load() {
        databaseManager = new AppleJsonDatabaseSingleton<>(
                PluginDecay.get().getFile("decayBlocks"),
                FileIOService.get()
        );
        @Nullable DecayBlockSettingsDatabase database = databaseManager.loadNow(DecayBlockSettingsDatabase.class, getSaveFileNameStatic());
        if (database == null) {
            instance = new DecayBlockSettingsDatabase();
            save();
        } else {
            instance = database;
        }
    }

    public static DecayBlockSettingsDatabase get() {
        return instance;
    }

    public static List<DecayBlockTemplateGroupingSettings> getAll() {
        return new ArrayList<>(get().settings.values());
    }

    private static void save() {
        databaseManager.save(instance);
    }

    @Nullable
    public static DecayBlockTemplateGroupingSettings get(UUID settingsUUID) {
        if (settingsUUID == null) return null;
        return get().settings.get(settingsUUID);
    }

    public static void add(DecayBlockTemplateGroupingSettings settings) {
        get().settings.put(settings.getUuid(), settings);
        save();
    }

    @NotNull
    private static String getSaveFileNameStatic() {
        return "decayBlockSettings.json";
    }

    @Override
    public String getSaveFileName() {
        return getSaveFileNameStatic();
    }

}
