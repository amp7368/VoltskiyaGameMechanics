package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import apple.mc.utilities.inventory.gui.acd.InventoryGuiACD;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;

public class MobTypeGui extends InventoryGuiACD {

    public MobTypeGui(TMWGui tmwGui, MobType.MobTypeBuilder mob) {
        addPage(
            new MobTypeGuiPageSettings(tmwGui, mob),
            new MobTypeGuiPageBiomes(tmwGui, mob),
            new MobTypeGuiPageSpawnConditions(tmwGui, mob)
        );
    }
}
