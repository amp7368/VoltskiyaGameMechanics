package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class MobTypeGuiPageSpawnConditions extends InventoryGuiPageScrollable {
    public MobTypeGuiPageSpawnConditions(MobTypeGui mobTypeGui, MobType.MobTypeBuilder mob) {
        super(mobTypeGui);
        setSlot(new InventoryGuiSlotGeneric((e) -> mobTypeGui.nextPage(-1), InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "Previous Page", null)
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

    @Override
    protected int getScrollIncrement() {
        return 8;
    }
}
