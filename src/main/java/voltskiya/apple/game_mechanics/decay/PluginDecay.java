package voltskiya.apple.game_mechanics.decay;

import apple.lib.pmc.PluginModule;
import apple.mc.utilities.PluginModuleMcUtil;
import apple.mc.utilities.player.wand.WandType;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDefaultsDatabase;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;
import voltskiya.apple.game_mechanics.decay.storage.DecaySqlStorage;
import voltskiya.apple.game_mechanics.decay.storage.MaterialDatabase;
import voltskiya.apple.game_mechanics.decay.world.WatchBlockBreak;

public class PluginDecay extends PluginModule implements PluginModuleMcUtil {

    public static WandType<DecayWand> DECAY_WAND;
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
        DecayBlockDefaultsDatabase.load();
        MaterialDatabase.load();
        DecayBlockSettingsDatabase.load();
        DecayBlockDatabase.load();
        new WatchBlockBreak();
        new DecayCommand();
        DECAY_WAND = createWand(DecayWand::new, "decay_wand");
        new DecaySqlStorage.SaveDaemon();
    }

    @Override
    public boolean shouldEnable() {
        return false;
    }

    @Override
    public String getName() {
        return "Decay";
    }
}
