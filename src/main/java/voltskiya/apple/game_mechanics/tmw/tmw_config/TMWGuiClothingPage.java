package voltskiya.apple.game_mechanics.tmw.tmw_config;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.gui.ClothingTypeGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGenericScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Comparator;
import java.util.List;

public class TMWGuiClothingPage extends InventoryGuiPageScrollable {

    private final TMWGui tmwGui;

    public TMWGuiClothingPage(TMWGui tmwGui) {
        super(tmwGui);
        this.tmwGui = tmwGui;
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric(e -> e.getWhoClicked().openInventory(new ClothingTypeGui(tmwGui, new ClothingType.ClothingTypeBuilder()).getInventory()),
                InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null)), 4);
        addClothing();
        setSlots();
    }

    private void addClothing() {
        clear();
        final List<ClothingType> clothings = ClothingDatabase.getAll();
        clothings.sort(Comparator.comparing(ClothingType::getName));
        for (ClothingType clothing : clothings) {
            add(new InventoryGuiSlotGenericScrollable(
                    e -> e.getWhoClicked().openInventory(new ClothingTypeGui(this.tmwGui, clothing.toBuilder()).getInventory()),
                    clothing.toItem()
            ));
        }
    }

    @Override
    public void setSlots() {
        addClothing();
        super.setSlots();
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }

    @Override
    public String getName() {
        return "All Clothing";
    }

    @Override
    public int size() {
        return 54;
    }
}
