package voltskiya.apple.game_mechanics.decay;

import voltskiya.apple.game_mechanics.VoltskiyaModule;
import voltskiya.apple.game_mechanics.decay.sql.DecaySqlStorage;
import voltskiya.apple.game_mechanics.decay.world.WatchBlockBreak;

public class PluginDecay extends VoltskiyaModule {
    @Override
    public void enable() {
        new WatchBlockBreak();
        new DecayCommand();
        new DecaySqlStorage.SaveDaemon();
    }

    @Override
    public String getName() {
        return "Decay";
    }
}
