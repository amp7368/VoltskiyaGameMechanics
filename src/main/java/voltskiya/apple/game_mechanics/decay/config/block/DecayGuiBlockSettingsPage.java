package voltskiya.apple.game_mechanics.decay.config.block;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.DecayGuiBlocksPage;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGenericScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Arrays;
import java.util.List;

public class DecayGuiBlockSettingsPage extends InventoryGuiPageScrollable {
    private final DecayBlockTemplate.DecayBlockBuilderTemplate block;

    public DecayGuiBlockSettingsPage(DecayGui decayGui, DecayGuiBlocksPage decayGuiBlocksPage, DecayBlockTemplate.DecayBlockBuilderTemplate block) {
        super(decayGui);
        this.block = block;
        setSlot(new InventoryGuiSlotGeneric(e -> {
        }, InventoryUtils.makeItem(block.icon, 1, "Block", null)
        ), 0);
        setSlot(new InventoryGuiSlotGeneric(e -> {
        }, InventoryUtils.makeItem(Material.BLACK_CONCRETE, 1, "Info",
                List.of("To set decay into",
                        "Right click a block in your inventory",
                        "To add a block",
                        "Left click a block in your inventory"
                ))), 5);
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
                InventoryUtils.makeItem(Material.LIME_TERRACOTTA, 1, "Save", null)
        ), 4);
    }

    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric(e -> {
            block.incrementDurability(e.getClick().isLeftClick() ? 1 : -1);
            update();
        }, InventoryUtils.makeItem(Material.OBSIDIAN, 1, "Durability " + block.getDurability(), Arrays.asList(
                "Left click - increase by 1",
                "Right click - decrease by 1"
        ))), 1);
        setSlot(new InventoryGuiSlotGeneric(e -> {
            block.incrementResistance(e.getClick().isLeftClick() ? 1 : -1);
            update();
        }, InventoryUtils.makeItem(Material.WOODEN_AXE, 1, "Damage " + block.getDurability(), Arrays.asList(
                "How much resistance does this item have against decay",
                "Left click - increase by 1",
                "Right click - decrease by 1"
        ))), 2);
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }

    @Override
    public void fillInventory() {
        addBlocks();
        setSlots();
        super.fillInventory();
    }

    private void addBlocks() {
        clear();
        for (Material material : block.materials) {
            add(new InventoryGuiSlotGenericScrollable(e -> {
                if (block.getIcon() != material) block.materials.remove(material);
                update();
            }, InventoryUtils.makeItem(material, 1, (String) null, null)
            ));
        }
    }

    @Override
    public void dealWithPlayerInventoryClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType().isBlock()) {
            if (event.getClick().isLeftClick()) {
                block.addMaterial(currentItem.getType());
            } else {
            }
            update();
        }
    }

    @Override
    public String getName() {
        return "Block Settings";
    }

    @Override
    public int size() {
        return 54;
    }
}
