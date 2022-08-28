package voltskiya.apple.game_mechanics.tmw;

import apple.configs.data.config.AppleConfig;
import apple.configs.factory.AppleConfigLike;
import apple.lib.pmc.PluginModule;
import apple.mc.utilities.PluginModuleMcUtil;
import java.util.List;
import voltskiya.apple.game_mechanics.tmw.commands.CommandMobsTmw;
import voltskiya.apple.game_mechanics.tmw.sql.TmwDatabaseConfig;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeUIDDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.ScanWorldConfig;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobRegen;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.PlayerTemperatureCommand;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.WorldDatabaseManager;

public class PluginTMW extends PluginModule implements PluginModuleMcUtil {

    public static final String MOBS_FOLDER = "mobs";
    public static final int BLOCKS_IN_A_CHUNK = 16;
    private static PluginTMW instance;
    private AppleConfig<?> tmwMobConfig;

    public PluginTMW() {
        instance = this;
    }

    public static PluginTMW get() {
        return instance;
    }

    @Override
    public void init() {
        tmwMobConfig = configJson(TmwMobConfigDatabase.class, "TmwMobConfig.json",
            PluginTMW.MOBS_FOLDER).build()[0];
        tmwMobConfig.register();
    }

    public void saveTmwMobConfig() {
        tmwMobConfig.save();
    }

    @Override
    public void enable() {
        BiomeUIDDatabase.load();
        TmwDatabaseConfig.load();
        WorldDatabaseManager.get().loadAllNow();
        BiomeTypeDatabase.load();
        VerifyDatabaseTmw.connect();
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
    public List<AppleConfigLike> getConfigs() {
        return List.of(configYaml(TmwWatchConfig.class, "TmwWatchConfig.yml"),
            configYaml(ScanWorldConfig.class, "ScanWorldConfig.yml")
        );
    }
}
