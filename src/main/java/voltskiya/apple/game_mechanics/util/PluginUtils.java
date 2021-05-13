package voltskiya.apple.game_mechanics.util;


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
        new UpdatedPlayerList();
    }

    @Override
    public String getName() {
        return "utilities";
    }
}
