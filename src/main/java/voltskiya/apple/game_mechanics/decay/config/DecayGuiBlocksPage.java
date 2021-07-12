package voltskiya.apple.game_mechanics.decay.config;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.block.DecayBlock;
import voltskiya.apple.game_mechanics.decay.config.block.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.block.DecayGuiBlockSettingsPage;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGenericScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Collections;

public class DecayGuiBlocksPage extends InventoryGuiPageScrollable {
    private DecayGui decayGui;

    public DecayGuiBlocksPage(DecayGui decayGui) {
        super(decayGui);
        this.decayGui = decayGui;
        setSlot(new InventoryGuiSlotGeneric(e -> {
        }, InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "To add a block",
                Collections.singletonList("Click a block in your inventory"))), 4);
        setSlot(new InventoryGuiSlotGeneric(e -> {
            decayGui.nextPage(-1);
        }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlots();
    }

    private void addBlocks() {
        clear();
        for (DecayBlock block : DecayBlockDatabase.getAll()) {
            add(new InventoryGuiSlotGenericScrollable(
                    e -> decayGui.setTempInventory(new DecayGuiBlockSettingsPage(decayGui, this, new DecayBlock.DecayBlockBuilder(block))),
                    InventoryUtils.makeItem(block.getMaterial(), 1, (String) null, null)
            ));
        }
    }

    @Override
    public void dealWithPlayerInventoryClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType().isBlock()) {
            decayGui.setTempInventory(new DecayGuiBlockSettingsPage(decayGui, this, new DecayBlock.DecayBlockBuilder(currentItem.getType())));
        }
    }

    @Override
    public void setSlots() {
        super.setSlots();
    }

    @Override
    public void fillInventory() {
        addBlocks();
        super.fillInventory();
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }

    @Override
    public String getName() {
        return "Blocks List";
    }

    @Override
    public int size() {
        return 54;
    }
}
