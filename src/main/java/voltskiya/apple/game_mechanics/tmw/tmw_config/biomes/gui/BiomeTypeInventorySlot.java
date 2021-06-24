package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;

public class BiomeTypeInventorySlot extends InventoryGuiSlotScrollable {
    private BiomeType biome;
    private TMWGui tmwGui;

    public BiomeTypeInventorySlot(BiomeType biome, TMWGui tmwGui) {
        this.biome = biome;
        this.tmwGui = tmwGui;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {
        event.getWhoClicked().openInventory(
                new BiomeTypeGui(tmwGui, biome.toBuilder()).getInventory()
        );
    }

    @Override
    public ItemStack getItem() {
        return biome.toItem();
    }
}
