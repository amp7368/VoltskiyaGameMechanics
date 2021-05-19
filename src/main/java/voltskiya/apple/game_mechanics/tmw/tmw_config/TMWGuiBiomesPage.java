package voltskiya.apple.game_mechanics.tmw.tmw_config;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

public class TMWGuiBiomesPage extends InventoryGuiPageScrollable {
    private TMWGui tmwGui;

    public TMWGuiBiomesPage(TMWGui tmwGui) {
        super(tmwGui);
        this.tmwGui = tmwGui;
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
    }
    @Override
    public String getName() {
        return "All Mobs";
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
