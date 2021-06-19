package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.util.gui.InventoryGui;

public class BiomeTypeGui extends InventoryGui {
    public BiomeTypeGui(TMWGui tmwGui, BiomeType.BiomeTypeBuilder biome) {
        addPage(
                new BiomeTypeGuiPageSettings(this, biome, tmwGui),
                new BiomeTypeGuiPageBlocks(this, biome, tmwGui),
                new BiomeTypeGuiPageMobs(this, biome, tmwGui)
        );
    }
}
