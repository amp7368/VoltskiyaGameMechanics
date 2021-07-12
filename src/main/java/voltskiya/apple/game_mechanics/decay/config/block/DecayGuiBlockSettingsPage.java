package voltskiya.apple.game_mechanics.decay.config.block;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.DecayGuiBlocksPage;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Arrays;
import java.util.Collections;

public class DecayGuiBlockSettingsPage extends InventoryGuiPageSimple {
    private DecayBlock.DecayBlockBuilder block;

    public DecayGuiBlockSettingsPage(DecayGui decayGui, DecayGuiBlocksPage decayGuiBlocksPage, DecayBlock.DecayBlockBuilder block) {
        super(decayGui);
        this.block = block;
        setSlot(new InventoryGuiSlotGeneric(e -> {
        }, InventoryUtils.makeItem(block.material, 1, "Block", null)
        ), 0);
        setSlot(new InventoryGuiSlotGeneric(e -> {
        }, InventoryUtils.makeItem(Material.BLACK_CONCRETE, 1, "To set decay into",
                Collections.singletonList("Click a block in your inventory"))), 2);
        setSlot(new InventoryGuiSlotGeneric(
                e -> decayGui.setTempInventory(null),
                InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "Back", null)
        ), 3);
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                    DecayBlockDatabase.addBlock(block.build());
                    decayGuiBlocksPage.update();
                    decayGui.setTempInventory(null);
                },
                InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "Save", null)
        ), 4);
    }

    private void setSlots() {
        setSlot(new InventoryGuiSlotGeneric(e -> {
            block.incrementDurability(e.getClick().isLeftClick() ? 1 : -1);
            update();
        }, InventoryUtils.makeItem(Material.OBSIDIAN, 1, "Durability " + block.getDurability(), Arrays.asList(
                "Left click - increase by 1",
                "Right click - decrease by 1"
        ))), 1);
        setSlot(new InventoryGuiSlotGeneric(e -> {
            block.decayInto = null;
            update();
        }, InventoryUtils.makeItem(block.decayInto == null ? Material.PETRIFIED_OAK_SLAB : block.decayInto,
                1, "Decay into (click to remove)", null)
        ), 8);
    }

    @Override
    public void fillInventory() {
        setSlots();
        super.fillInventory();
    }

    @Override
    public void dealWithPlayerInventoryClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType().isBlock()) {
            block.decayInto = currentItem.getType();
            update();
        }
    }

    @Override
    public String getName() {
        return "Block Settings";
    }

    @Override
    public int size() {
        return 9;
    }
}
