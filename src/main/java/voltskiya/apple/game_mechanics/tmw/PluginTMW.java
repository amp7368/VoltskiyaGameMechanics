package voltskiya.apple.game_mechanics.tmw;

import voltskiya.apple.game_mechanics.VoltskiyaModule;
import voltskiya.apple.game_mechanics.tmw.sql.TmwSqlVerifyDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobRegen;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.PlayerTemperatureCommand;

public class PluginTMW extends VoltskiyaModule {
    private static PluginTMW instance;

    public static PluginTMW get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        TmwSqlVerifyDatabase.connect();
        new TMWCommand();
        new MobListener();
        new WatchPlayerListener();
        new MobRegen();
        new PlayerTemperatureCommand();
    }

    @Override
    public String getName() {
        return "tmw";
    }
}
