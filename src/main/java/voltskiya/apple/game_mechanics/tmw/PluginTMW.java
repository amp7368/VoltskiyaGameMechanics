package voltskiya.apple.game_mechanics.tmw;

import voltskiya.apple.game_mechanics.VoltskiyaModule;

public class PluginTMW extends VoltskiyaModule {
    private static PluginTMW instance;

    public static PluginTMW get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        new TMWCommand();
    }

    @Override
    public String getName() {
        return "tmw";
    }
}
