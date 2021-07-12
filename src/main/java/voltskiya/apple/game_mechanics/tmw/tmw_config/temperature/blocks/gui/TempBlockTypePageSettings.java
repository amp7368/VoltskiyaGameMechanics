package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TempBlockType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TemperatureBlocksDatabase;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Arrays;
import java.util.Collections;

public class TempBlockTypePageSettings extends InventoryGuiPageSimple {
    private final TempBlockType.TempBlockTypeBuilder blockType;

    public TempBlockTypePageSettings(TempBlockTypeGui blockTypeGui, TempBlockType.TempBlockTypeBuilder blockType, TMWGui tmwGui) {
        super(blockTypeGui);
        this.blockType = blockType;
        setSlots();
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                    TemperatureBlocksDatabase.addTempBlock(blockType.build());
                    tmwGui.update(null);
                    e.getWhoClicked().openInventory(tmwGui.getInventory());
                }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)
        ), 8);

        setSlot(new InventoryGuiSlotGeneric((e1) -> e1.getWhoClicked().openInventory(tmwGui.getInventory()), InventoryUtils.makeItem(Material.LIGHT_BLUE_TERRACOTTA, 1, "Back", null)
        ), 6);
        setSlot(new InventoryGuiSlotGeneric(e -> {
            if (e.getClick().isLeftClick()) {
                blockType.incrementTemp(-1);
            } else {
                blockType.incrementTemp(1);
            }
            update();
        }, InventoryUtils.makeItem(Material.REDSTONE, 1, "Temperature",
                Arrays.asList(
                        "Left click - decrease temperature by 1",
                        "Right click - increase temperature by 1"
                ))), 1);
    }

    private void setSlots() {
        final Material material = blockType.getMaterial();
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                }, InventoryUtils.makeItem(
                material.isItem() ? material : Material.BLACK_CONCRETE,
                1,
                material.name(),
                Collections.singletonList(String.format("%.2f output", blockType.getTemperature()))
        )
        ), 0);
    }

    @Override
    public void fillInventory() {
        setSlots();
        super.fillInventory();
    }

    @Override
    public String getName() {
        return "Temperature block";
    }

    @Override
    public void dealWithPlayerInventoryClick(InventoryClickEvent event) {
        ItemStack itemClicked = event.getCurrentItem();
        if (itemClicked != null && itemClicked.getType().isBlock()) {
            blockType.setType(itemClicked.getType());
        }
        update();
    }

    @Override
    public int size() {
        return 9;
    }
}
