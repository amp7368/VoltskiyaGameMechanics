package voltskiya.apple.game_mechanics.tmw.tmw_config;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.gui.ClothingTypeGui;

public class TMWGuiClothingPage extends InventoryGuiPageScrollableACD<TMWGui> {


    public TMWGuiClothingPage(TMWGui parent) {
        super(parent);
        setSlot(slotImpl((e1) -> parentPrev(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(slotImpl((e1) -> parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(slotImpl(e -> e.getWhoClicked().openInventory(
                new ClothingTypeGui(parent, new ClothingType.ClothingTypeBuilder()).getInventory()),
            makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null)), 4);
    }

    @Override
    public void refreshPageItems() {
        clear();
        final List<ClothingType> clothings = ClothingDatabase.getAll();
        clothings.sort(Comparator.comparing(ClothingType::getName));
        for (ClothingType clothing : clothings) {
            add(slotImpl(e -> e.getWhoClicked().openInventory(
                    new ClothingTypeGui(this.parent, clothing.toBuilder()).getInventory()),
                clothing.toItem()));
        }
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
