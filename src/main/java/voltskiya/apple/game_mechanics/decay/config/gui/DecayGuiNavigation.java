package voltskiya.apple.game_mechanics.decay.config.gui;

import org.bukkit.Material;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageImplACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class DecayGuiNavigation extends InventoryGuiPageImplACD<DecayGui> {
    public DecayGuiNavigation(DecayGui decayGui) {
        super(decayGui);
    }

    @Override
    public void initialize() {
        setSlot(new InventoryGuiSlotImplACD(e -> parentAddSubPage(new DecayGuiListBlocksPage(parent)),
                InventoryUtils.makeItem(Material.BRICKS, "Blocks list")), 1);
        setSlot(new InventoryGuiSlotImplACD(e -> parentAddSubPage(new DecayGuiListBlockSettingsPage(parent)),
                InventoryUtils.makeItem(Material.REDSTONE_TORCH, "Block settings list")), 4);
        setSlot(new InventoryGuiSlotImplACD(e -> parentAddSubPage(new DecayGuiSettingsPage(parent)),
                InventoryUtils.makeItem(Material.COMPARATOR, "General settings")), 7);
    }

    @Override
    public String getName() {
        return "Decay Navigation";
    }

    @Override
    public int size() {
        return 27;
    }
}
