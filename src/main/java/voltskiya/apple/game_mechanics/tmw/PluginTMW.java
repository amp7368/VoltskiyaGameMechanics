package voltskiya.apple.game_mechanics.tmw;

import voltskiya.apple.game_mechanics.VoltskiyaModule;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.BiomeWatchPlayerListener;

public class PluginTMW extends VoltskiyaModule {
    private static PluginTMW instance;

    public static PluginTMW get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        new TMWCommand();
        new BiomeWatchPlayerListener();
    }

    @Override
    public String getName() {
        return "tmw";
    }
}
