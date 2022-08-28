package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType.MobTypeBuilder;

public class MobTypeGuiPageSpawnConditions extends InventoryGuiPageScrollableACD<TMWGui> {

    public MobTypeGuiPageSpawnConditions(TMWGui mobTypeGui, MobTypeBuilder mob) {
        super(mobTypeGui);
        setSlot(slotImpl((e) -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, 1, "Previous Page", null)
        ), 0);
    }

    @Override
    public String getName() {
        return "Special spawn conditions";
    }

    @Override
    public int size() {
        return 54;
    }
}
