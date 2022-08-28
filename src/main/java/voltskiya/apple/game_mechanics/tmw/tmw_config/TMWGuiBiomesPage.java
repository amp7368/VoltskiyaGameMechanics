package voltskiya.apple.game_mechanics.tmw.tmw_config;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeGui;

public class TMWGuiBiomesPage extends InventoryGuiPageScrollableACD<TMWGui> {


    public TMWGuiBiomesPage(TMWGui tmwGui) {
        super(tmwGui);
    }


    @Override
    public void refreshPageItems() {
        clear();
        for (BiomeType biome : BiomeTypeDatabase.getAll()) {
            add(slotImpl((e) -> parentAddSubPage(new BiomeTypeGui(parent, biome.toBuilder())),
                biome.toItem()));
        }
    }

    @Override
    public void initialize() {
        setSlot(slotImpl((e1) -> parentPrev(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(slotImpl((e1) -> parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(slotImpl(
            e -> parentAddSubPage(new BiomeTypeGui(parent, new BiomeType.BiomeTypeBuilder())),
            makeItem(Material.DARK_OAK_SAPLING, 1, "Add a biome", null)), 4);
    }


    @Override
    public String getName() {
        return "All Biomes";
    }

    @Override
    public int size() {
        return 54;
    }
}
