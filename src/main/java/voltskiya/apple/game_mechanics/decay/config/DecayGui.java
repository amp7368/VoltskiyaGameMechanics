package voltskiya.apple.game_mechanics.decay.config;

import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class DecayGui extends InventoryGui {
    public DecayGui(Player player) {
        addPage(
                new DecayGuiSettingsPage(this),
                new DecayGuiBlocksPage(this)
        );
    }
}
