package voltskiya.apple.game_mechanics.decay;

import voltskiya.apple.game_mechanics.VoltskiyaModule;
import voltskiya.apple.game_mechanics.decay.sql.DecaySqlStorage;
import voltskiya.apple.game_mechanics.decay.world.WatchBlockBreak;
import voltskiya.apple.utilities.util.wand.WandToolList;

public class PluginDecay extends VoltskiyaModule {
    @Override
    public void enable() {
        new WatchBlockBreak();
        new DecayCommand();
        WandToolList.addWand(DecayWand.WAND_KEY, DecayWand::new);
        new DecaySqlStorage.SaveDaemon();
    }

    @Override
    public String getName() {
        return "Decay";
    }
}
