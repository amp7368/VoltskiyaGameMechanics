package voltskiya.apple.game_mechanics.tmw;

import voltskiya.apple.game_mechanics.VoltskiyaModule;
import voltskiya.apple.game_mechanics.tmw.sql.TmwSqlVerifyDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobListener;

import java.sql.SQLException;

public class PluginTMW extends VoltskiyaModule {
    private static PluginTMW instance;

    public static PluginTMW get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        try {
            TmwSqlVerifyDatabase.connect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        new TMWCommand();
        new MobListener();
        new WatchPlayerListener();
    }

    @Override
    public String getName() {
        return "tmw";
    }
}
