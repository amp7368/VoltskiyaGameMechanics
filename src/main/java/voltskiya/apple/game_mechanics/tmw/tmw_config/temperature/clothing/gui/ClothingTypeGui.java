package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.gui;

import apple.mc.utilities.inventory.gui.acd.InventoryGuiACD;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingType;

public class ClothingTypeGui extends InventoryGuiACD {

    public ClothingTypeGui(TMWGui parent, ClothingType.ClothingTypeBuilder clothing) {
        addPage(new ClothingTypeGuiPageSettings(parent, clothing));
    }
}
