package voltskiya.apple.game_mechanics;


import apple.lib.pmc.ApplePlugin;
import apple.lib.pmc.PluginModule;
import java.util.List;
import voltskiya.apple.game_mechanics.decay.PluginDecay;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

public class VoltskiyaPlugin extends ApplePlugin {

    private static VoltskiyaPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    public static VoltskiyaPlugin get() {
        return instance;
    }

    @Override
    public List<PluginModule> getModules() {
        return List.of(new PluginTMW(), new PluginDecay());
    }
}
