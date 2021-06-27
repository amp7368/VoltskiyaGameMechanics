package voltskiya.apple.utilities.util;


import voltskiya.apple.game_mechanics.VoltskiyaModule;

public class PluginUtils extends VoltskiyaModule {
    private static PluginUtils instance;

    public static PluginUtils get() {
        return instance;
    }

    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
    }

    @Override
    public String getName() {
        return "utilities";
    }
}
