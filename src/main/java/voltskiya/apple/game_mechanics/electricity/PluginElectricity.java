package voltskiya.apple.game_mechanics.electricity;

import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.game_mechanics.electricity.piping.major.ElectricityMajorLinesAll;
import voltskiya.apple.game_mechanics.electricity.piping.simple.ElectricitySimpleLinesAll;
import voltskiya.apple.game_mechanics.electricity.placement.PlayerInteractElectric;

public class PluginElectricity extends PluginManagedModule {
    private static PluginElectricity instance;

    public static PluginElectricity get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        ElectricityMajorLinesAll.initialize();
        ElectricitySimpleLinesAll.initialize();
        new CommandElectricity();
        new PlayerInteractElectric();
    }

    @Override
    public String getName() {
        return "electricity";
    }
}
