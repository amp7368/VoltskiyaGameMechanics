package voltskiya.apple.game_mechanics.tmw.tmw_config;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TempBlockType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TemperatureBlocksDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.gui.TempBlockTypeGui;

public class TMWGuiBlocksPage extends InventoryGuiPageScrollableACD<TMWGui> {


    public TMWGuiBlocksPage(TMWGui parent) {
        super(parent);
        setSlot(slotImpl((e1) -> parentPrev(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(slotImpl((e1) -> parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(slotImpl(e -> e.getWhoClicked().openInventory(new TempBlockTypeGui(this.parent,
                new TempBlockType.TempBlockTypeBuilder()).getInventory()),
            makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null)), 4);
    }

    @Override
    public void refreshPageItems() {
        clear();
        final List<TempBlockType> blocks = TemperatureBlocksDatabase.getAll();
        blocks.sort(Comparator.comparing(TempBlockType::getName));
        for (TempBlockType block : blocks) {
            add(slotImpl(e -> parentAddSubPage(new TempBlockTypeGui(parent, block.toBuilder())),
                block.toItem()));
        }
    }

    @Override
    public String getName() {
        return "Temperature blocks";
    }

    @Override
    public int size() {
        return 54;
    }
}
