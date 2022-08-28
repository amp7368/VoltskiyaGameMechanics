package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.gui;

import apple.mc.utilities.inventory.gui.acd.InventoryGuiACD;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TempBlockType;

public class TempBlockTypeGui extends InventoryGuiACD {

    public TempBlockTypeGui(TMWGui parent, TempBlockType.TempBlockTypeBuilder blockType) {
        addPage(new TempBlockTypePageSettings(parent, blockType));
    }
}
