package voltskiya.apple.game_mechanics.decay;

import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDefaultsDatabase;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;
import voltskiya.apple.game_mechanics.decay.storage.DecaySqlStorage;
import voltskiya.apple.game_mechanics.decay.storage.MaterialDatabase;
import voltskiya.apple.game_mechanics.decay.world.WatchBlockBreak;
import voltskiya.apple.utilities.util.wand.WandToolList;

public class PluginDecay extends PluginManagedModule {
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
        WandToolList.addWand(DecayWand.WAND_KEY, DecayWand::new);
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
