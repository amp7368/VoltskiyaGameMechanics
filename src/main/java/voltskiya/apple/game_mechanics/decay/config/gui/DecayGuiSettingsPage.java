package voltskiya.apple.game_mechanics.decay.config.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDefaultsDatabase;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageConfigACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.gui.acd.slotannotation.ClickACD;
import voltskiya.apple.utilities.util.gui.acd.slotannotation.GuiButtonACD;
import voltskiya.apple.utilities.util.gui.acd.slotannotation.GuiNameSupplier;
import voltskiya.apple.utilities.util.gui.acd.slotannotation.ItemACD;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;


public class DecayGuiSettingsPage extends InventoryGuiPageConfigACD<DecayGui> {
    public DecayGuiSettingsPage(DecayGui decayGui) {
        super(decayGui);
    }

    @Override
    public void initialize() {
        setSlot(new InventoryGuiSlotImplACD(e -> parentRemoveSubPage(),
                InventoryUtils.makeItem(Material.RED_TERRACOTTA, "Back to navigation")), 0);
    }

    @GuiButtonACD(item = @ItemACD(material = Material.JUNGLE_SAPLING, name = "Decay Rate: %s", nameSupplier = "getDecayRate"),
            onClick = {
                    @ClickACD(isLeft = true, description = "Left click - increment decay rate", valIfTrue = 1),
                    @ClickACD(isRight = true, description = "Right click - decrement decay rate", valIfTrue = -1)
            },
            slots = 1
    )
    public void incrementDecay(InventoryClickEvent event, int val) {
        DecayBlockDefaultsDatabase.incrementDecayRate(val);
    }

    @GuiButtonACD(item = @ItemACD(material = Material.CRACKED_STONE_BRICKS, name = "Decay Rate: %s", nameSupplier = "getDurability"),
            onClick = {
                    @ClickACD(isLeft = true, description = "Left click - increment durability rate", valIfTrue = 1),
                    @ClickACD(isRight = true, description = "Right click - decrement durability rate", valIfTrue = -1)
            },
            slots = 2
    )
    public void incrementDurability(InventoryClickEvent event, int val) {
        DecayBlockDefaultsDatabase.incrementDurability(val);
    }

    @GuiNameSupplier(nameSupplier = "getDecayRate")
    public Object[] getDecayRateNameSupplier() {
        return new Object[]{DecayBlockDefaultsDatabase.getDecayRate()};
    }

    @GuiNameSupplier(nameSupplier = "getDurability")
    public Object[] getDurabilityNameSupplier() {
        return new Object[]{DecayBlockDefaultsDatabase.getDurability()};
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
