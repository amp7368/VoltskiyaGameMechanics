package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.util.gui.InventoryGui;

public class MobTypeGui extends InventoryGui {
    public MobTypeGui(TMWGui tmwGui,MobTypeBuilder mob) {
        addPage(
                new MobTypeGuiPageSettings(this,mob)
        );
    }
}
