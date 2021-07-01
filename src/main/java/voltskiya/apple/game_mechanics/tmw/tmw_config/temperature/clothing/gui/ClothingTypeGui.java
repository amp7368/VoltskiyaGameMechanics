package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.gui;

import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingType;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class ClothingTypeGui extends InventoryGui {
    public ClothingTypeGui(TMWGui tmwGui, ClothingType.ClothingTypeBuilder clothing) {
        addPage(new ClothingTypeGuiPageSettings(tmwGui, this, clothing));
    }
}
