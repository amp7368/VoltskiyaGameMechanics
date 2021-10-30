package voltskiya.apple.game_mechanics.tmw;

import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.game_mechanics.tmw.commands.CommandMobsTmw;
import voltskiya.apple.game_mechanics.tmw.sql.TmwDatabaseConfig;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobRegen;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.PlayerTemperatureCommand;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.WorldDatabaseManager;

public class PluginTMW extends PluginManagedModule {
    public static final String MOBS_FOLDER = "mobs";
    private static PluginTMW instance;

    public static PluginTMW get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

        TmwDatabaseConfig.load();
        WorldDatabaseManager.get().loadAllNow();
        VerifyDatabaseTmw.connect();
        MobConfigDatabase.loadNow();

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
}
