package voltskiya.apple.game_mechanics.decay.config.gui;

import voltskiya.apple.utilities.util.gui.acd.InventoryGuiACD;

public class DecayGui extends InventoryGuiACD {
    public DecayGui() {
        addPage(new DecayGuiNavigation(this));
    }
}
