package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class MobTypeGui extends InventoryGui {
    public MobTypeGui(TMWGui tmwGui, MobType.MobTypeBuilder mob) {
        addPage(
                new MobTypeGuiPageSettings(this,mob,tmwGui),
                new MobTypeGuiPageBiomes(this,mob),
                new MobTypeGuiPageSpawnConditions(this,mob)
        );
    }
}
