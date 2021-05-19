package voltskiya.apple.game_mechanics.tmw;

import voltskiya.apple.game_mechanics.VoltskiyaModule;

public class PluginTMW extends VoltskiyaModule {
    @Override
    public void enable() {
        new TMWCommand();
    }

    @Override
    public String getName() {
        return "tmw";
    }
}
