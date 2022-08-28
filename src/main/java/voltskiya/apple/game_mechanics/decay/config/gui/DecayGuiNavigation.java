package voltskiya.apple.game_mechanics.decay.config.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import org.bukkit.Material;

public class DecayGuiNavigation extends InventoryGuiPageImplACD<DecayGui> {

    public DecayGuiNavigation(DecayGui decayGui) {
        super(decayGui);
    }

    @Override
    public void initialize() {
        setSlot(slotImpl(e -> parentAddSubPage(new DecayGuiListBlocksPage(parent)),
            makeItem(Material.BRICKS, "Blocks list")), 1);
        setSlot(slotImpl(e -> parentAddSubPage(new DecayGuiListBlockSettingsPage(parent)),
            makeItem(Material.REDSTONE_TORCH, "Block settings list")), 4);
        setSlot(slotImpl(e -> parentAddSubPage(new DecayGuiSettingsPage(parent)),
            makeItem(Material.COMPARATOR, "General settings")), 7);
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
