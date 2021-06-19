package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

public class MobTypeGuiPageBiomes extends InventoryGuiPageScrollable {
    private MobTypeGui mobTypeGui;
    private MobType.MobTypeBuilder mob;

    public MobTypeGuiPageBiomes(MobTypeGui mobTypeGui, MobType.MobTypeBuilder mob) {
        super(mobTypeGui);
        this.mobTypeGui = mobTypeGui;
        this.mob = mob;
        setSlot(new InventoryGuiSlotGeneric((e) -> mobTypeGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric((e) -> mobTypeGui.nextPage(-1), InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "Previous Page", null)
        ), 0);
    }

    @Override
    public String getName() {
        return "Biomes this mob spawns in";
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
