package voltskiya.apple.game_mechanics.decay.config;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.config.block.DecayBlockDatabase;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Arrays;

public class DecayGuiSettingsPage extends InventoryGuiPageSimple {
    public DecayGuiSettingsPage(DecayGui decayGui) {
        super(decayGui);
        setSlot(new InventoryGuiSlotGeneric(e -> {
            decayGui.nextPage(1);
        }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
    }

    @Override
    public void fillInventory() {
        setSlots();
        super.fillInventory();
    }

    private void setSlots() {
        setSlot(new InventoryGuiSlotGeneric(e -> {
            DecayBlockDatabase.incrementDecayRate(e.getClick().isLeftClick() ? 1 : -1);
            update();
        }, InventoryUtils.makeItem(Material.JUNGLE_SAPLING, 1, "Decay Rate " + DecayBlockDatabase.getDecayRate(), Arrays.asList(
                "Left click - increment decay rate",
                "Right click - decrement decay rate"
        ))), 1);
        setSlot(new InventoryGuiSlotGeneric(e -> {
            DecayBlockDatabase.incrementDurability(e.getClick().isLeftClick() ? 1 : -1);
            update();
        }, InventoryUtils.makeItem(Material.CRACKED_STONE_BRICKS, 1, "Default durability " + DecayBlockDatabase.getDurability(), Arrays.asList(
                "Left click - increment durability",
                "Right click - decrement durability"
        ))), 2);
    }

    @Override
    public String getName() {
        return "Decay Settings";
    }

    @Override
    public int size() {
        return 9;
    }
}
