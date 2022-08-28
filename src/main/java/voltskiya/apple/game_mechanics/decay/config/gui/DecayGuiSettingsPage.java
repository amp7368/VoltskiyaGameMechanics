package voltskiya.apple.game_mechanics.decay.config.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDefaultsDatabase;


public class DecayGuiSettingsPage extends InventoryGuiPageImplACD<DecayGui> {

    public DecayGuiSettingsPage(DecayGui decayGui) {
        super(decayGui);
    }

    @Override
    public void initialize() {
        setSlot(slotImpl(e -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, "Back to navigation")), 0);
    }

    //    @GuiButtonACD(item = @ItemACD(material = Material.JUNGLE_SAPLING, name = "Decay Rate: %s", nameSupplier = "getDecayRate"),
//            onClick = {
//                    @ClickACD(isLeft = true, description = "Left click - increment decay rate", valIfTrue = 1),
//                    @ClickACD(isRight = true, description = "Right click - decrement decay rate", valIfTrue = -1)
//            },
//            slots = 1
//    )
    public void incrementDecay(InventoryClickEvent event, int val) {
        DecayBlockDefaultsDatabase.incrementDecayRate(val);
    }

    //    @GuiButtonACD(item = @ItemACD(material = Material.CRACKED_STONE_BRICKS, name = "Decay Rate: %s", nameSupplier = "getDurability"),
//            onClick = {
//                    @ClickACD(isLeft = true, description = "Left click - increment durability rate", valIfTrue = 1),
//                    @ClickACD(isRight = true, description = "Right click - decrement durability rate", valIfTrue = -1)
//            },
//            slots = 2
//    )
    public void incrementDurability(InventoryClickEvent event, int val) {
        DecayBlockDefaultsDatabase.incrementDurability(val);
    }

    //    @GuiNameSupplier(nameSupplier = "getDecayRate")
    public Object[] getDecayRateNameSupplier() {
        return new Object[]{DecayBlockDefaultsDatabase.getDecayRate()};
    }

    //    @GuiNameSupplier(nameSupplier = "getDurability")
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
