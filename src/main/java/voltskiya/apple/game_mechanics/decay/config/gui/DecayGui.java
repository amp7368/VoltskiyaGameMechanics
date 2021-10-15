package voltskiya.apple.game_mechanics.decay.config.gui;

import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.acd.InventoryGuiACD;

public class DecayGui extends InventoryGuiACD {
    public DecayGui(Player player) {
        addPage(
                new DecayGuiSettingsPage(this),
                new DecayGuiListBlocksPage(this)
        );
    }
}
