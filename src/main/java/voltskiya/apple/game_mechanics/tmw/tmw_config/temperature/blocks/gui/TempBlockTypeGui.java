package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.gui;

import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TempBlockType;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TempBlockTypeGui extends InventoryGui {
    public TempBlockTypeGui(TMWGui tmwGui, TempBlockType.TempBlockTypeBuilder blockType) {
        addPage(
                new TempBlockTypePageSettings(this, blockType, tmwGui)
        );
    }
}
