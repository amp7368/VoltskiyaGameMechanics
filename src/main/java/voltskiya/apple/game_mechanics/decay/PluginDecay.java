package voltskiya.apple.game_mechanics.decay;

import voltskiya.apple.game_mechanics.VoltskiyaModule;
import voltskiya.apple.game_mechanics.decay.config.block.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.storage.DecaySqlStorage;
import voltskiya.apple.game_mechanics.decay.storage.MaterialDatabase;
import voltskiya.apple.game_mechanics.decay.world.WatchBlockBreak;
import voltskiya.apple.utilities.util.wand.WandToolList;

public class PluginDecay extends VoltskiyaModule {
    private static PluginDecay instance;

    public static PluginDecay get() {
        return instance;
    }

    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        MaterialDatabase.load();
        DecayBlockDatabase.load();
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
