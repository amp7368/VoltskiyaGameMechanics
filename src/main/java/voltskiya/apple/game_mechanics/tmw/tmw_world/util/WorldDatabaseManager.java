package voltskiya.apple.game_mechanics.tmw.tmw_world.util;

import apple.utilities.database.queue.AppleJsonDatabaseManager;
import apple.utilities.request.AppleRequestQueue;
import apple.utilities.request.settings.RequestSettingsBuilder;
import apple.utilities.request.settings.RequestSettingsBuilderVoid;
import apple.utilities.util.FileFormatting;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.util.FileIOService;

import java.io.File;

public class WorldDatabaseManager implements AppleJsonDatabaseManager<SimpleWorldDatabase> {
    private static final WorldDatabaseManager instance = new WorldDatabaseManager();

    public static WorldDatabaseManager get() {
        return instance;
    }

    @Override
    public RequestSettingsBuilder<SimpleWorldDatabase> getLoadingSettings() {
        return RequestSettingsBuilder.empty();
    }

    @Override
    public File getDBFolder() {
        return FileFormatting.fileWithChildren(PluginTMW.get().getDataFolder(), "sharedData");
    }

    @Override
    public AppleRequestQueue getIOService() {
        return FileIOService.get();
    }

    @Override
    public RequestSettingsBuilderVoid getSavingSettings() {
        return RequestSettingsBuilderVoid.VOID;
    }
}
