package voltskiya.apple.game_mechanics.tmw.tmw_config;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TempBlockType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TemperatureBlocksDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.gui.TempBlockTypeGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGenericScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Comparator;
import java.util.List;

public class TMWGuiBlocksPage extends InventoryGuiPageScrollable {

    private final TMWGui tmwGui;

    public TMWGuiBlocksPage(TMWGui tmwGui) {
        super(tmwGui);
        this.tmwGui = tmwGui;
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric(e -> e.getWhoClicked().openInventory(new TempBlockTypeGui(tmwGui, new TempBlockType.TempBlockTypeBuilder()).getInventory()),
                InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null)), 4);
        addBlocks();
        setSlots();
    }

    private void addBlocks() {
        clear();
        final List<TempBlockType> blocks = TemperatureBlocksDatabase.getAll();
        blocks.sort(Comparator.comparing(TempBlockType::getName));
        for (TempBlockType block : blocks) {
            add(new InventoryGuiSlotGenericScrollable(
                    e -> e.getWhoClicked().openInventory(new TempBlockTypeGui(this.tmwGui, block.toBuilder()).getInventory()),
                    block.toItem()
            ));
        }
    }

    @Override
    public void setSlots() {
        addBlocks();
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
        return "Temperature blocks";
    }

    @Override
    public int size() {
        return 54;
    }
}
