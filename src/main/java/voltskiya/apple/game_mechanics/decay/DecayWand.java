package voltskiya.apple.game_mechanics.decay;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.utilities.util.wand.WandPlayer;
import voltskiya.apple.utilities.util.wand.WandUse;


public class DecayWand implements WandPlayer {
    public static final NamespacedKey WAND_KEY = new NamespacedKey(VoltskiyaPlugin.get(), "decay_wand");
    private int size = 5;

    public DecayWand(Player player) {
    }

    @Override
    @WandUse(action = {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK})
    public void dealWithUse(PlayerInteractEvent event, String wandItemMetaz) {

    }

    public void setSize(int size) {
        this.size = size;
    }
}
