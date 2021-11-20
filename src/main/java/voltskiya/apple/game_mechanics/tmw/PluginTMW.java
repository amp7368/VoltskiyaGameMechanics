package voltskiya.apple.game_mechanics.tmw;

import apple.utilities.util.FileFormatting;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.configs.plugin.manage.ConfigBuilderHolder;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;
import voltskiya.apple.game_mechanics.tmw.commands.CommandMobsTmw;
import voltskiya.apple.game_mechanics.tmw.sql.TmwDatabaseConfig;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobRegen;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.PlayerTemperatureCommand;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.WorldDatabaseManager;

import java.util.Collection;
import java.util.List;

public class PluginTMW extends PluginManagedModule implements PluginManagedModuleConfig {
    public static final String MOBS_FOLDER = "mobs";
    private static PluginTMW instance;

    public static PluginTMW get() {
        return instance;
    }

    public PluginTMW() {
        instance = this;
    }

    @Override
    public void init() {
        TmwMobConfigDatabase.initialize();
    }

    @Override
    public void enable() {
        TmwDatabaseConfig.load();
        WorldDatabaseManager.get().loadAllNow();
        VerifyDatabaseTmw.connect();
        TmwMobConfigDatabase.loadNow();

        new MobListener();
        new WatchPlayerListener();
        new MobRegen();

        new PlayerTemperatureCommand();
        new TMWCommand();
        new CommandMobsTmw();
    }

    @Override
    public String getName() {
        return "tmw";
    }

    @Override
    public Collection<ConfigBuilderHolder<?>> getConfigsToRegister() {
        return List.of(configFolder(
                yml(TmwWatchConfig.class).setName("TmwWatchConfig").setExtension(FileFormatting::extensionYml),
                basic(TmwMobConfigDatabase.class)
                        .setLoading(TmwMobConfigDatabase::loadNow)
                        .setName(TmwMobConfigDatabase.getFileNameExtStatic())
        ).nameAsExtension());
    }
}
