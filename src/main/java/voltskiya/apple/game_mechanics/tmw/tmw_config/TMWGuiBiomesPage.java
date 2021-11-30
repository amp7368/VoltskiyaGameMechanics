package voltskiya.apple.game_mechanics.tmw.tmw_config;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeInventorySlot;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class TMWGuiBiomesPage extends InventoryGuiPageScrollable {
    private final TMWGui tmwGui;

    public TMWGuiBiomesPage(TMWGui tmwGui) {
        super(tmwGui);
        this.tmwGui = tmwGui;
        addBiomes();
        setSlots();
    }


    private void addBiomes() {
        clear();
        for (BiomeType biome : BiomeTypeDatabase.getAll()) {
            add(new BiomeTypeInventorySlot(biome, tmwGui));
        }
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric(e -> e.getWhoClicked().openInventory(new BiomeTypeGui(tmwGui, new BiomeType.BiomeTypeBuilder()).getInventory()),
                InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "Add a biome", null)), 4);
    }

    @Override
    public void fillInventory() {
        addBiomes();
        super.fillInventory();
    }

    @Override
    public String getName() {
        return "All Biomes";
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }
}
