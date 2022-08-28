package voltskiya.apple.game_mechanics.decay.config.gui;

import apple.mc.utilities.inventory.gui.acd.InventoryGuiACD;

public class DecayGui extends InventoryGuiACD {

    public DecayGui() {
        addPage(new DecayGuiNavigation(this));
    }
}
