package voltskiya.apple.game_mechanics.tmw;

import voltskiya.apple.game_mechanics.VoltskiyaModule;
import voltskiya.apple.game_mechanics.tmw.sql.TmwDatabaseConfig;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobRegen;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.PlayerTemperatureCommand;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.WorldDatabaseManager;

public class PluginTMW extends VoltskiyaModule {
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

        new MobListener();
        new WatchPlayerListener();
        new MobRegen();

        new PlayerTemperatureCommand();
        new TMWCommand();
    }

    @Override
    public String getName() {
        return "tmw";
    }
}
