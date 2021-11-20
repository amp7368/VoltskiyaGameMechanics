package voltskiya.apple.game_mechanics;


import plugin.util.plugin.plugin.util.plugin.PluginManaged;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.configs.plugin.manage.PluginManagedConfigRegister;
import voltskiya.apple.game_mechanics.decay.PluginDecay;
import voltskiya.apple.game_mechanics.electricity.PluginElectricity;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.util.Collection;
import java.util.List;

public class VoltskiyaPlugin extends PluginManaged implements PluginManagedConfigRegister {
    private static VoltskiyaPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    @Override
    public void initialize() {
        registerAllConfigs();
    }

    @Override
    public Collection<PluginManagedModule> getModules() {
        return List.of(
                new PluginTMW(),
                new PluginDecay(),
                new PluginElectricity()
        );
    }

    public static VoltskiyaPlugin get() {
        return instance;
    }
}
