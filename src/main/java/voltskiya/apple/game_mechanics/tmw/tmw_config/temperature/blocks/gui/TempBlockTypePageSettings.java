package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import java.util.Arrays;
import java.util.Collections;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TempBlockType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TemperatureBlocksDatabase;

public class TempBlockTypePageSettings extends InventoryGuiPageImplACD<TMWGui> {

    private final TempBlockType.TempBlockTypeBuilder blockType;

    public TempBlockTypePageSettings(TMWGui blockTypeGui,
        TempBlockType.TempBlockTypeBuilder blockType) {
        super(blockTypeGui);
        this.blockType = blockType;
        setSlot(slotImpl(e -> {
            TemperatureBlocksDatabase.addTempBlock(blockType.build());
            parentRemoveSubPage();
        }, makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)), 8);

        setSlot(slotImpl((e1) -> parentRemoveSubPage(),
            makeItem(Material.LIGHT_BLUE_TERRACOTTA, 1, "Back", null)), 6);
        setSlot(slotImpl(e -> {
            if (e.getClick().isLeftClick()) {
                blockType.incrementTemp(-1);
            } else {
                blockType.incrementTemp(1);
            }
        }, makeItem(Material.REDSTONE, 1, "Temperature",
            Arrays.asList("Left click - decrease temperature by 1",
                "Right click - increase temperature by 1"))), 1);
    }

    @Override
    public void refreshPageItems() {
        final Material material = blockType.getMaterial();
        setSlot(slotImpl(e -> {
            }, makeItem(material.isItem() ? material : Material.BLACK_CONCRETE, 1, material.name(),
                Collections.singletonList(String.format("%.2f output", blockType.getTemperature())))),
            0);
    }


    @Override
    public String getName() {
        return "Temperature block";
    }

    @Override
    public void onPlayerInventory(InventoryClickEvent event) {
        ItemStack itemClicked = event.getCurrentItem();
        if (itemClicked != null && itemClicked.getType().isBlock()) {
            blockType.setType(itemClicked.getType());
        }
        refresh();
    }

    @Override
    public int size() {
        return 9;
    }
}
